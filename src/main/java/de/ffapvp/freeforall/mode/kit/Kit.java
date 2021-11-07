/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.ffapvp.freeforall.mode.kit;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
public class Kit {

    private List<KitItem> kitItems;

    public static class Builder {

        public static Builder create() {
            return new Kit.Builder();
        }

        private List<KitItem> kitItems;

        Builder() {
            this.kitItems = new ArrayList<>();
        }

        public Builder add(KitItem kitItem) {
            this.kitItems.add(kitItem);
            return this;
        }

        public Builder adds(KitItem... kitItem) {
            for (KitItem item : kitItem) {
                this.add(item);
            }
            return this;
        }

        public Kit build() {
            return new Kit(this.kitItems);
        }
    }
}
