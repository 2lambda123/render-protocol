package hiforce.render.protocol.engine;

import hiforce.render.protocol.engine.request.RenderPageReqDTO;
import hiforce.render.protocol.engine.response.RenderPageRespDTO;
import hiforce.render.protocol.model.IPageRenderResult;
import hiforce.render.protocol.model.RenderFunction;

import java.util.function.Function;

/**
 * 多端渲染协议渲染引擎，主要用于界面渲染与提交
 *
 * @author Rocky Yu
 * @since 2020/8/12
 */
public interface RenderEngine {

    /**
     * 渲染页面，获取组件化渲染数据
     *
     * @param request  RenderPageReqDTO
     * @param function 自定义的处理逻辑脚手架
     * @return RenderPageRespDTO
     */
    RenderPageRespDTO adjustRenderPage(RenderPageReqDTO request, Function<RenderFunction, IPageRenderResult> function);


    /**
     * 渲染页面，获取组件化渲染数据，一般用于第一次渲染，没有任何界面数据的操作
     *
     * @param function 自定义的处理逻辑脚手架
     * @return RenderPageRespDTO
     */
    RenderPageRespDTO renderPage(Function<RenderFunction, IPageRenderResult> function);

    /**
     * 提交页面
     *
     * @param request  RenderPageReqDTO
     * @param function 自定义的处理逻辑脚手架
     * @return RenderPageRespDTO
     */
    RenderPageRespDTO submitPage(RenderPageReqDTO request, Function<RenderFunction, IPageRenderResult> function);
}
