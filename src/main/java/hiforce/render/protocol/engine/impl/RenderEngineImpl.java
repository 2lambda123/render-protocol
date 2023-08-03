package hiforce.render.protocol.engine.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import hiforce.render.protocol.engine.RenderEngine;
import hiforce.render.protocol.engine.request.RenderPageReqDTO;
import hiforce.render.protocol.engine.response.RenderPageRespDTO;
import hiforce.render.protocol.model.BaseCO;
import hiforce.render.protocol.model.Data;
import hiforce.render.protocol.model.Endpoint;
import hiforce.render.protocol.model.Hierarchy;
import hiforce.render.protocol.model.IPageRenderResult;
import hiforce.render.protocol.model.IQueryParam;
import hiforce.render.protocol.model.ISubmitParam;
import hiforce.render.protocol.model.Linkage;
import hiforce.render.protocol.model.RenderData;
import hiforce.render.protocol.model.RenderFunction;
import hiforce.render.protocol.model.config.StructureConfig;
import hiforce.render.protocol.model.config.StructureItem;
import hiforce.render.protocol.spi.ComponentBuilderSpi;
import hiforce.render.protocol.spi.RenderSpiFactory;
import hiforce.render.protocol.utils.CompressUtils;
import hiforce.render.protocol.utils.JacksonUtils;
import org.hiforce.lattice.exception.LatticeRuntimeException;
import org.hiforce.lattice.message.Message;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author Rocky Yu
 * @since 2020/8/12
 */
@Slf4j
@Service
public class RenderEngineImpl implements RenderEngine {

    private <T> T deCompressData(boolean compress, String json, Class<T> targetClass) throws Exception {
        if (compress) {
            return JacksonUtils.deserialize(CompressUtils.decompress(json), targetClass);
        }
        return JacksonUtils.deserialize(json, targetClass);
    }

    private RenderFunction parseRenderFunction(RenderPageReqDTO request) throws Exception {
        RenderFunction renderFunction = new RenderFunction();
        if (StringUtils.isNotEmpty(request.getData())) {
            renderFunction.setData(JacksonUtils.deserialize(request.getData(), Data.class));
        }
        if (StringUtils.isNotEmpty(request.getLinkage())) {
            Linkage linkage = JacksonUtils.deserialize(request.getLinkage(), Linkage.class);
            renderFunction.setLinkage(linkage);
            if (null != linkage && StringUtils.isNotEmpty(linkage.getQueryParams())) {
                renderFunction.setQueryParam(
                        deCompressData(linkage.isCompress(), linkage.getQueryParams(), IQueryParam.class));
            }
            if (null != linkage && StringUtils.isNotEmpty(linkage.getSubmitParams())) {
                renderFunction.setSubmitParam(
                        deCompressData(linkage.isCompress(), linkage.getSubmitParams(), ISubmitParam.class));
            }
        }
        if (StringUtils.isNotEmpty(request.getHierarchy())) {
            Hierarchy hierarchy = JacksonUtils.deserialize(request.getHierarchy(), Hierarchy.class);
            renderFunction.setHierarchy(hierarchy);
        }
        return renderFunction;
    }

    @Override
    public RenderPageRespDTO adjustRenderPage(RenderPageReqDTO request, Function<RenderFunction, IPageRenderResult> function) {
        RenderFunction renderFunction;
        //Step 1：把数据从请求中解析出来，并把JSON转换成实际对象
        try {
            renderFunction = parseRenderFunction(request);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Message message = Message.code("HF-RENDER-ENGINE-002", request.toString());
            return RenderPageRespDTO.failed("HF-RENDER-ENGINE-002", message.getDisplayText());
        }

        return renderPage(true, renderFunction, function);
    }

    private void attachQueryParam(RenderFunction render) {
        if (null == render) {
            return;
        }
        if (null == render.getQueryParam()) {
            return;
        }
        if (null == render.getData() || MapUtils.isEmpty(render.getData())) {
            return;
        }
        render.getData().forEach((key, value) -> value.attachQueryParam(render.getQueryParam()));
    }

    private void makeupSubmitParam(RenderFunction render) {
        if (null == render) {
            return;
        }
        if (null == render.getQueryParam()) {
            return;
        }
        if (null == render.getData() || MapUtils.isEmpty(render.getData())) {
            return;
        }
        render.getData().forEach((key, value) -> value.makeupSubmitParam(render, render.getSubmitParam()));
    }

    private RenderPageRespDTO renderPage(boolean adjustRender, RenderFunction render, Function<RenderFunction, IPageRenderResult> function) {
        //Step 1: 调用渲染处理逻辑之前，可以先做查询参数丰富
        attachQueryParam(render);

        //Step 2: 调用业务系统的后台处理逻辑，获取标准化的页面渲染结果
        IPageRenderResult result = function.apply(render); //这里得到ShoppingResult

        //Step 3：根据渲染结果，进行组件话数据封装
        RenderPageRespDTO respDTO = buildRenderPageRespDTO(result);

        //针对异步刷新页面情况，需要过滤出仅仅变化的组件给前端
        if (respDTO.isSuccess() &&
                isRenderDataCanBeCompressed(adjustRender, render, result, respDTO)) {//如果是异步渲染，并且不是全量重新加载页面
            //过滤出签名变化了的组件
            filterSignatureNotChangedComponents(render.getHierarchy(),
                    respDTO.getRenderData().getHierarchy(), respDTO.getRenderData());
        }
        return respDTO;
    }

    private void filterSignatureNotChangedComponents(Hierarchy origin, Hierarchy current, RenderData data) {
        List<String> output = Lists.newArrayList();
        for (String id : current.getSignatureMap().keySet()) {
            String newSign = current.getSignatureMap().get(id);
            String originSign = origin.getSignatureMap().get(id);
            if (StringUtils.equals(newSign, originSign)) {
                output.add(id);
            }
        }
        output.forEach(p -> data.getData().remove(p));
    }

    private boolean isRenderDataCanBeCompressed(
            boolean adjustRender, RenderFunction render, IPageRenderResult result, RenderPageRespDTO respDTO) {
        if (!adjustRender || result.isReloadPage()) {
            return false;
        }
        if (null == render.getHierarchy() || null == respDTO.getRenderData().getHierarchy()) {
            return false;
        }
        //判断页面结构是否有变化，如果页面结构有变化，也不做数据精简
        return !isStructureChanged(render.getHierarchy().getStructure(),
                respDTO.getRenderData().getHierarchy().getStructure());
    }

    private boolean isStructureChanged(Map<String, List<String>> origin, Map<String, List<String>> current) {
        if (null == origin || null == current) {
            return true;
        }
        if (origin.keySet().size() != current.keySet().size()) {
            return true;
        }
        return origin.keySet().stream()
                .anyMatch(key -> isStructureItemChanged(origin.get(key), current.get(key)));
    }

    private boolean isStructureItemChanged(List<String> origin, List<String> current) {
        if (CollectionUtils.isEmpty(origin) && CollectionUtils.isEmpty(current)) {
            return false;
        }
        if (CollectionUtils.isEmpty(origin) || CollectionUtils.isEmpty(current)) {
            return true;
        }
        if (origin.size() != current.size()) {
            return true;
        }
        for (String item : origin) {
            if (current.stream().noneMatch(p -> StringUtils.equals(p, item))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public RenderPageRespDTO renderPage(Function<RenderFunction, IPageRenderResult> function) {
        return renderPage(false, new RenderFunction(), function);
    }

    @Override
    public RenderPageRespDTO submitPage(RenderPageReqDTO request, Function<RenderFunction, IPageRenderResult> function) {
        RenderFunction renderFunction;
        //Step 1：把数据从请求中解析出来，并把JSON转换成实际对象
        try {
            renderFunction = parseRenderFunction(request);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Message message = Message.code("HF-RENDER-ENGINE-002", request.toString());
            return RenderPageRespDTO.failed("HF-RENDER-ENGINE-002", message.getDisplayText());
        }

        makeupSubmitParam(renderFunction);
        //Step 2: 调用业务系统的后台处理逻辑，获取标准化的页面渲染结果
        IPageRenderResult result = function.apply(renderFunction);
        //Step 3：根据渲染结果，进行组件话数据封装
        return buildRenderPageRespDTO(result);//构建页面提交的展示页面
    }

    private List<BaseCO> buildRenderComponents(StructureConfig config, IPageRenderResult result) {
        StructureItem root = config.getRoot();
        List<BaseCO> outputs = buildStructureItemCOs(result, root, null);
        for (BaseCO baseCO : outputs) {
            if (baseCO.getClass().isAnonymousClass()) {
                throw new LatticeRuntimeException("HF-RENDER-ENGINE-001", baseCO.getTag());
            }
            if (Modifier.isAbstract(baseCO.getClass().getModifiers())) {
                throw new LatticeRuntimeException("HF-RENDER-ENGINE-001", baseCO.getTag());
            }
        }
        return outputs;
    }

    private static Linkage buildRenderLinkage(List<BaseCO> components, IPageRenderResult result) {
        try {
            Linkage linkage = new Linkage();

            linkage.setQueryParams(CompressUtils.compress(JacksonUtils.serialize(result.getQueryParam())));
            linkage.setSubmitParams(CompressUtils.compress(
                    JacksonUtils.serialize(buildCreateOrderReqDTO(components, result))));
            linkage.setCompress(true);
            linkage.getInputs().addAll(
                    components.stream().filter(BaseCO::isInput)
                            .map(BaseCO::getUniqueId).collect(Collectors.toList())
            );
            linkage.getRequests().addAll(
                    components.stream().filter(BaseCO::isRequest)
                            .map(BaseCO::getUniqueId).collect(Collectors.toList())
            );
            linkage.setUrl(components.stream().filter(BaseCO::isWithUrl).findFirst()
                    .map(BaseCO::getUrl).orElse(null));
            return linkage;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new LatticeRuntimeException("HF-RENDER-ENGINE-003");
        }
    }

    private static ISubmitParam buildCreateOrderReqDTO(List<BaseCO> components, IPageRenderResult result) {
        ISubmitParam submitParam = result.createSubmitParam();
        if (null != submitParam) {
            components.forEach(p -> p.attachSubmitParam(result, submitParam));
        }
        return submitParam;
    }

    private static Endpoint buildRenderEndpoint(IPageRenderResult result) {
        return new Endpoint();
    }

    private static Hierarchy buildRenderHierarchy(List<BaseCO> components, IPageRenderResult result) {

        Hierarchy hierarchy = new Hierarchy();
        hierarchy.getComponents().addAll(
                components.stream().map(BaseCO::getUniqueId).collect(Collectors.toList()));
        components.forEach(p -> hierarchy.getSignatureMap().put(p.getUniqueId(), p.getSignature()));

        components.stream().filter(
                p -> StringUtils.equals(p.getTag(), result.getStructureConfig().getRoot().getTag())
        ).findFirst().ifPresent(rootCO -> hierarchy.setRoot(rootCO.getUniqueId()));

        StructureItem rootItem = result.getStructureConfig().getRoot();
        hierarchy.setStructure(buildPageStructureInstance(rootItem, components));
        return hierarchy;
    }

    private static Map<String, List<String>> buildPageStructureInstance(StructureItem item, List<BaseCO> components) {
        Map<String, List<String>> structures = Maps.newHashMap();
        List<BaseCO> matchedItem = components.stream().filter(p -> StringUtils.equals(item.getTag(), p.getTag()))
                .collect(Collectors.toList());
        for (BaseCO baseCO : matchedItem) {
            List<String> children = Lists.newArrayList();
            children.addAll(components.stream()
                    .filter(p -> item.getChildren().stream()
                            .anyMatch(pp ->
                                    StringUtils.equals(pp.getTag(), p.getTag())
                                            && StringUtils.equals(baseCO.getId(), p.getParentId())
                            )
                    )
                    .map(BaseCO::getUniqueId)
                    .collect(Collectors.toList()));
            if (CollectionUtils.isNotEmpty(children)) {
                structures.put(baseCO.getUniqueId(), children);
            }
            item.getChildren().forEach(p -> structures.putAll(buildPageStructureInstance(p, components)));
        }
        return structures;
    }


    /**
     * 根据用户注册的
     *
     * @param pageResult IPageRenderResult
     * @param config     StructureConfig
     */
    private void enrichPageStructureConfig(IPageRenderResult pageResult, StructureConfig config) {

        List<BaseCO> customCOs = new ArrayList<>(pageResult.getComponents());

        for (BaseCO pageCO : customCOs) {
            if (config.getRoot().getAllItemTags().contains(pageCO.getTag())) {
                continue;
            }
            StructureItem parentItem = config.getRoot().getItemByTag(pageCO.getParentTag());
            if (parentItem.getAllItemTags().contains(pageCO.getTag())) {
                continue;
            }
            parentItem.addItem(StructureItem.of(pageCO.getTag()));
        }
    }

    @SuppressWarnings("all")
    private List<BaseCO> buildStructureItemCOs(IPageRenderResult result, StructureItem item, BaseCO parent) {
        List<ComponentBuilderSpi> builders =
                RenderSpiFactory.getInstance().getComponentBuilderByTag(item.getTag());
        if (CollectionUtils.isEmpty(builders)) {
            return Lists.newArrayList();
        }
        List<BaseCO> outputs = Lists.newArrayList();
        for (ComponentBuilderSpi builder : builders) {
            List<BaseCO> baseCOs = buildComponents(item.getTag(), result, builder, parent);
            if (null != parent) {
                baseCOs.forEach(p -> p.setParentId(parent.getId()));
            }
            outputs.addAll(baseCOs);
            for (BaseCO baseCO : baseCOs) {
                outputs.addAll(item.getChildren().stream()
                        .flatMap(p -> buildStructureItemCOs(result, p, baseCO).stream())
                        .collect(Collectors.toList()));
            }
        }
        return outputs;
    }

    @SuppressWarnings("all")
    private List<BaseCO> buildComponents(String tag, IPageRenderResult result, ComponentBuilderSpi builder, BaseCO parent) {
        if (!builder.isSupportRoot(result)) {
            return Lists.newArrayList();
        }
        List<Object> targetObjs = builder.extract(tag, result, parent);
        return (List<BaseCO>) targetObjs.stream()
                .filter(Objects::nonNull)
            .filter(p -> builder.isSupportResult(p))
                .map(p -> (BaseCO) builder.build(tag, result, p, parent))
                .collect(Collectors.toList());
    }

    private RenderPageRespDTO buildRenderPageRespDTO(IPageRenderResult result) {
        RenderData renderData = new RenderData();
        if (null == result) {
            return RenderPageRespDTO.success(renderData);
        }
        renderData.setReload(result.isReloadPage());

        StructureConfig structureConfig = result.getStructureConfig();
        if (null != structureConfig) {
            enrichPageStructureConfig(result, structureConfig);
            List<BaseCO> components = buildRenderComponents(structureConfig, result);
            //构建页面渲染组件信息
            components.forEach(p -> renderData.getData().put(p.getUniqueId(), p));

            //构建页面结构实例信息
            renderData.setHierarchy(buildRenderHierarchy(components, result));

            //构建页面接入信息
            renderData.setEndpoint(buildRenderEndpoint(result));

            //构建页面上的Linkage信息
            renderData.setLinkage(buildRenderLinkage(components, result));
        }
        //构建页面所有组件的签名信息
        return RenderPageRespDTO.success(renderData);
    }
}
