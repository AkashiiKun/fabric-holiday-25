package holiday;

import java.util.concurrent.CompletableFuture;

import holiday.item.HolidayServerItems;
import holiday.tag.HolidayServerItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

public class HolidayServerItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public HolidayServerItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registries) {
        super(output, registries);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries) {
        this.valueLookupBuilder(HolidayServerItemTags.HOPPER_TRAPPED)
                .add(HolidayServerItems.HOPPER_MITE);

        this.valueLookupBuilder(ItemTags.PIGLIN_LOVED)
                .add(HolidayServerItems.GOLDEN_HOPPER);
    }
}
