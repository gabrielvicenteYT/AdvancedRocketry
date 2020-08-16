package zmaster587.advancedRocketry.integration.jei.lathe;

import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import zmaster587.advancedRocketry.integration.jei.ARPlugin;
import zmaster587.advancedRocketry.integration.jei.MachineCategoryTemplate;
import zmaster587.advancedRocketry.integration.jei.MachineRecipe;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.LibVulpes;

public class LatheCategory extends MachineCategoryTemplate<MachineRecipe> {

	public LatheCategory(IGuiHelper helper, ItemStack icon) {
		super(helper, TextureResources.latheProgressBar, icon);
	}
	
	@Override
	public ResourceLocation getUid() {
		return ARPlugin.latheUUID;
	}

	@Override
	public Class<? extends MachineRecipe> getRecipeClass() {
		return MachineRecipe.class;
	}
	
	@Override
	public String getTitle() {
		return LibVulpes.proxy.getLocalizedString("tile.lathe.name");
	}
}
