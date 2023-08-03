package hiforce.render.protocol.model.config;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Rocky Yu
 * @since 2020/8/12
 */
public class StructureItem {

    @Getter
    @Setter
    private String tag;

    @Getter
    private final List<StructureItem> children = Lists.newArrayList();

    public static StructureItem of(String tag) {
        StructureItem item = new StructureItem();
        item.tag = tag;
        return item;
    }

    public StructureItem addItem(StructureItem... items) {
        if (null == items || 0 == items.length) {
            return this;
        }
        children.addAll(Arrays.asList(items));
        return this;
    }

    public StructureItem getItemByTag(String tag) {
        if (StringUtils.equals(this.tag, tag)) {
            return this;
        }
        for (StructureItem item : children) {
            StructureItem found = item.getItemByTag(tag);
            if (null != found) {
                return found;
            }
        }
        return null;
    }

    public List<String> getAllItemTags() {
        List<String> tags = Lists.newArrayList();
        tags.add(tag);
        tags.addAll(children.stream().flatMap(p -> p.getAllItemTags().stream())
            .collect(Collectors.toList()));
        return tags;
    }
}
