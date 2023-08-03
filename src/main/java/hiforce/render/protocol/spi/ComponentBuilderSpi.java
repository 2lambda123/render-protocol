package hiforce.render.protocol.spi;


import hiforce.render.protocol.model.BaseCO;
import hiforce.render.protocol.utils.ClassUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @author Rocky
 * @since 2020/8/13
 */
public interface ComponentBuilderSpi<Root, Result, Component extends BaseCO> {

    /**
     * @return 当前的组件构造器支持何种组件
     */
    boolean supportTag(String tag);

    default boolean isSupportRoot(Root rootInstance) {
        Type actualTypeArgument = ((ParameterizedTypeImpl) getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
        return ClassUtils.isSubClassOf(rootInstance.getClass(), (Class) actualTypeArgument);
    }

    default boolean isSupportResult(Result resultInstance) {
        Type actualTypeArgument = ((ParameterizedTypeImpl) getClass().getGenericInterfaces()[0]).getActualTypeArguments()[1];
        return ClassUtils.isSubClassOf(resultInstance.getClass(), (Class) actualTypeArgument);
    }

    /**
     * 当前组件构造器从根节点上如何解析出需要构造的组件原始模型
     *
     * @param root 根节点对象
     * @return 目标组件原始对象
     */
    List<Result> extract(String tag, Root root, BaseCO parent);

    /**
     * 构造组件
     *
     * @param root  根节点模型
     * @param model 目标组件原始对象
     * @return 生成的目标组件对象
     */
    Component build(String tag, Root root, Result model, BaseCO parent);
}
