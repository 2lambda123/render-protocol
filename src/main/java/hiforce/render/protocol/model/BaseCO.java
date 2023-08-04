package hiforce.render.protocol.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import hiforce.render.protocol.utils.JacksonUtils;
import hiforce.render.protocol.utils.MD5;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hiforce.lattice.exception.LatticeRuntimeException;

import java.io.Serializable;

/**
 * @author Rocky Yu
 * @since 2020/8/12
 */
@Slf4j
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "$class")
public class BaseCO implements Serializable {

    private static final long serialVersionUID = -6614183254610692475L;

    @Getter
    @Setter
    private String id;

    @Setter
    @Getter
    private String tag;

    @Getter
    @Setter
    private String parentTag;

    /**
     * 父组件的instance id
     */
    @Setter
    @Getter
    private String parentId;

    /**
     * 当前组件数据是否提交
     */
    @Getter
    @Setter
    private boolean submit = true;

    /**
     * 可输入内容
     */
    @Getter
    @Setter
    @JsonIgnore
    private boolean input = false;

    /**
     * 有变动是否需要请求
     */
    @Getter
    @Setter
    @JsonIgnore
    private boolean request = false;

    @Getter
    @Setter
    private String type; //比如格式: native$checkbox,  h5$checkbox 等

    /**
     * 是否携带异步请求url，一个页面只能有一个组件携带
     */
    @Getter
    @Setter
    @JsonIgnore
    private boolean withUrl = false;

    /**
     * 异步请求url
     */
    @Getter
    @Setter
    @JsonIgnore
    private String url;

    public BaseCO(String tag, String id) {
        this.tag = tag;
        this.id = id;
    }

    public String getUniqueId() {
        return String.format("%s_%s", tag, id);
    }

    /**
     * @param renderParam 把当前组件的值，Attach到RenderParam上，用于本次查询参数的更新
     */
    public void attachRenderParam(IRenderParam renderParam) {

    }

    /**
     * 基于当前的组件渲染结果，补充到提交参数中（用于页面提交使用）
     *
     * @param result      IPageRenderResult
     * @param submitParam ISubmitParam
     */
    public void attachSubmitParam(IPageRenderResult result, ISubmitParam submitParam) {

    }

    /**
     * 基于本次提交的组件数据，把组件数据补充到提交参数中
     *
     * @param render      本次的页面提交数据
     * @param submitParam 当前的提交参数
     */
    public void makeupSubmitParam(RenderFunction render, ISubmitParam submitParam) {

    }

    public String getSignature() {
        try {
            String json = JacksonUtils.serialize(this);
            return MD5.getInstance().getMD5String(json);
        } catch (Exception e) {
            throw new LatticeRuntimeException("HF-RENDER-ENGINE-004");
        }
    }
}
