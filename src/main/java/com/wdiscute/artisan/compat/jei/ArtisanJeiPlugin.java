package com.wdiscute.artisan.compat.jei;

import com.wdiscute.artisan.Artisan;
import com.wdiscute.artisan.ChancedStack;
import com.wdiscute.artisan.registry.ArtisanBlocks;
import com.wdiscute.artisan.registry.ArtisanRecipeTypes;
import com.wdiscute.utils.MaybeStack;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JeiPlugin
public class ArtisanJeiPlugin implements IModPlugin
{
    @Override
    public void registerCategories(IRecipeCategoryRegistration registration)
    {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();

        //register categories
        registration.addRecipeCategories(
                new ArtisanJeiLoomRecipe(guiHelper, ArtisanBlocks.LOOM),
                new ArtisanJeiCheesePressRecipe(guiHelper, ArtisanBlocks.CHEESE_PRESS),
                new ArtisanJeiWineKegRecipe(guiHelper, ArtisanBlocks.WINE_KEG),
                new ArtisanJeiAgingCaskRecipe(guiHelper, ArtisanBlocks.AGING_CASK),
                new ArtisanJeiAncientCaskRecipe(guiHelper, ArtisanBlocks.ANCIENT_CASK),
                new ArtisanJeiCrystalariumRecipe(guiHelper, ArtisanBlocks.CRYSTALARIUM),
                new ArtisanJeiDeluxeWormFarmRecipe(guiHelper, ArtisanBlocks.DELUXE_WORM_FARM),
                new ArtisanJeiFishSmokerRecipe(guiHelper, ArtisanBlocks.FISH_SMOKER),
                new ArtisanJeiDehydratorRecipe(guiHelper, ArtisanBlocks.DEHYDRATOR),
                new ArtisanJeiMayonnaiseMachineRecipe(guiHelper, ArtisanBlocks.MAYONNAISE_MACHINE),
                new ArtisanJeiPreservesJarRecipe(guiHelper, ArtisanBlocks.PRESERVES_JAR),
                new ArtisanJeiSeedMakerRecipe(guiHelper, ArtisanBlocks.SEED_MAKER),
                new ArtisanJeiRecyclingMachineRecipe(guiHelper, ArtisanBlocks.RECYCLING_MACHINE),
                new ArtisanJeiBaitMakerRecipe(guiHelper, ArtisanBlocks.BAIT_MAKER),
                new ArtisanJeiOilMakerRecipe(guiHelper, ArtisanBlocks.OIL_MAKER),
                new ArtisanJeiChargingRodRecipe(guiHelper, ArtisanBlocks.CHARGING_ROD)
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration)
    {
        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();

        registration.addRecipes(ArtisanJeiLoomRecipe.TYPE,
                manager.getAllRecipesFor(ArtisanRecipeTypes.LOOM.get())
                        .stream().map(ArtisanJeiLoomRecipe.Recipe::of).toList());

        registration.addRecipes(ArtisanJeiCheesePressRecipe.TYPE,
                manager.getAllRecipesFor(ArtisanRecipeTypes.CHEESE_PRESS.get())
                        .stream().map(ArtisanJeiCheesePressRecipe.Recipe::of).toList());

        registration.addRecipes(ArtisanJeiWineKegRecipe.TYPE,
                manager.getAllRecipesFor(ArtisanRecipeTypes.WINE_KEG.get())
                        .stream().map(ArtisanJeiWineKegRecipe.Recipe::of).toList());

        registration.addRecipes(ArtisanJeiAgingCaskRecipe.TYPE,
                manager.getAllRecipesFor(ArtisanRecipeTypes.AGING_CASK.get())
                        .stream().map(ArtisanJeiAgingCaskRecipe.Recipe::of).toList());

        registration.addRecipes(ArtisanJeiAncientCaskRecipe.TYPE,
                manager.getAllRecipesFor(ArtisanRecipeTypes.ANCIENT_CASK.get())
                        .stream().map(ArtisanJeiAncientCaskRecipe.Recipe::of).toList());

        registration.addRecipes(ArtisanJeiCrystalariumRecipe.TYPE,
                manager.getAllRecipesFor(ArtisanRecipeTypes.CRYSTALARIUM.get())
                        .stream().map(ArtisanJeiCrystalariumRecipe.Recipe::of).toList());

        registration.addRecipes(ArtisanJeiDeluxeWormFarmRecipe.TYPE,
                manager.getAllRecipesFor(ArtisanRecipeTypes.DELUXE_WORM_FARM.get())
                        .stream().map(ArtisanJeiDeluxeWormFarmRecipe.Recipe::of).toList());

        registration.addRecipes(ArtisanJeiFishSmokerRecipe.TYPE,
                manager.getAllRecipesFor(ArtisanRecipeTypes.FISH_SMOKER.get())
                        .stream().map(ArtisanJeiFishSmokerRecipe.Recipe::of).toList());

        registration.addRecipes(ArtisanJeiDehydratorRecipe.TYPE,
                manager.getAllRecipesFor(ArtisanRecipeTypes.DEHYDRATOR.get())
                        .stream().map(ArtisanJeiDehydratorRecipe.Recipe::of).toList());

        registration.addRecipes(ArtisanJeiMayonnaiseMachineRecipe.TYPE,
                manager.getAllRecipesFor(ArtisanRecipeTypes.MAYONNAISE_MACHINE.get())
                        .stream().map(ArtisanJeiMayonnaiseMachineRecipe.Recipe::of).toList());

        registration.addRecipes(ArtisanJeiPreservesJarRecipe.TYPE,
                manager.getAllRecipesFor(ArtisanRecipeTypes.PRESERVES_JAR.get())
                        .stream().map(ArtisanJeiPreservesJarRecipe.Recipe::of).toList());

        registration.addRecipes(ArtisanJeiSeedMakerRecipe.TYPE,
                manager.getAllRecipesFor(ArtisanRecipeTypes.SEED_MAKER.get())
                        .stream().map(ArtisanJeiSeedMakerRecipe.Recipe::of).toList());

        registration.addRecipes(ArtisanJeiRecyclingMachineRecipe.TYPE,
                manager.getAllRecipesFor(ArtisanRecipeTypes.RECYCLING_MACHINE.get())
                        .stream().map(ArtisanJeiRecyclingMachineRecipe.Recipe::of).toList());

        registration.addRecipes(ArtisanJeiBaitMakerRecipe.TYPE,
                manager.getAllRecipesFor(ArtisanRecipeTypes.BAIT_MAKER.get())
                        .stream().map(ArtisanJeiBaitMakerRecipe.Recipe::of).toList());

        registration.addRecipes(ArtisanJeiOilMakerRecipe.TYPE,
                manager.getAllRecipesFor(ArtisanRecipeTypes.OIL_MAKER.get())
                        .stream().map(ArtisanJeiOilMakerRecipe.Recipe::of).toList());

        registration.addRecipes(ArtisanJeiChargingRodRecipe.TYPE,
                manager.getAllRecipesFor(ArtisanRecipeTypes.CHARGING_ROD.get())
                        .stream().map(ArtisanJeiChargingRodRecipe.Recipe::of).toList());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration)
    {
        registration.addRecipeCatalyst(ArtisanBlocks.LOOM.asItem().getDefaultInstance(), ArtisanJeiLoomRecipe.TYPE);
        registration.addRecipeCatalyst(ArtisanBlocks.CHEESE_PRESS.asItem().getDefaultInstance(), ArtisanJeiCheesePressRecipe.TYPE);
        registration.addRecipeCatalyst(ArtisanBlocks.WINE_KEG.asItem().getDefaultInstance(), ArtisanJeiWineKegRecipe.TYPE);
        registration.addRecipeCatalyst(ArtisanBlocks.AGING_CASK.asItem().getDefaultInstance(), ArtisanJeiAgingCaskRecipe.TYPE);
        registration.addRecipeCatalyst(ArtisanBlocks.ANCIENT_CASK.asItem().getDefaultInstance(), ArtisanJeiAncientCaskRecipe.TYPE);
        registration.addRecipeCatalyst(ArtisanBlocks.CRYSTALARIUM.asItem().getDefaultInstance(), ArtisanJeiCrystalariumRecipe.TYPE);
        registration.addRecipeCatalyst(ArtisanBlocks.DELUXE_WORM_FARM.asItem().getDefaultInstance(), ArtisanJeiDeluxeWormFarmRecipe.TYPE);
        registration.addRecipeCatalyst(ArtisanBlocks.FISH_SMOKER.asItem().getDefaultInstance(), ArtisanJeiFishSmokerRecipe.TYPE);
        registration.addRecipeCatalyst(ArtisanBlocks.DEHYDRATOR.asItem().getDefaultInstance(), ArtisanJeiDehydratorRecipe.TYPE);
        registration.addRecipeCatalyst(ArtisanBlocks.MAYONNAISE_MACHINE.asItem().getDefaultInstance(), ArtisanJeiMayonnaiseMachineRecipe.TYPE);
        registration.addRecipeCatalyst(ArtisanBlocks.PRESERVES_JAR.asItem().getDefaultInstance(), ArtisanJeiPreservesJarRecipe.TYPE);
        registration.addRecipeCatalyst(ArtisanBlocks.SEED_MAKER.asItem().getDefaultInstance(), ArtisanJeiSeedMakerRecipe.TYPE);
        registration.addRecipeCatalyst(ArtisanBlocks.RECYCLING_MACHINE.asItem().getDefaultInstance(), ArtisanJeiRecyclingMachineRecipe.TYPE);
        registration.addRecipeCatalyst(ArtisanBlocks.BAIT_MAKER.asItem().getDefaultInstance(), ArtisanJeiBaitMakerRecipe.TYPE);
        registration.addRecipeCatalyst(ArtisanBlocks.OIL_MAKER.asItem().getDefaultInstance(), ArtisanJeiOilMakerRecipe.TYPE);
        registration.addRecipeCatalyst(ArtisanBlocks.CHARGING_ROD.asItem().getDefaultInstance(), ArtisanJeiChargingRodRecipe.TYPE);
    }


    @Override
    public ResourceLocation getPluginUid()
    {
        return Artisan.rl("jei_plugin");
    }

    public static List<ChancedStack> condenseOutputs(List<ChancedStack> outputs)
    {
        List<ChancedStack> result = new ArrayList<>(outputs.size());

        for (ChancedStack chanced : outputs)
        {
            MaybeStack stack = chanced.stack();
            if (stack.isEmpty()) continue;

            if (chanced.chance() >= 1)
            {
                boolean merged = false;
                for (int i = 0; i < result.size(); i++)
                {
                    ChancedStack existing = result.get(i);
                    MaybeStack es = existing.stack();

                    if (existing.chance() >= 1
                        && es.identifier().equals(stack.identifier())
                        && es.patch().equals(stack.patch()))
                    {
                        result.set(i, new ChancedStack(
                                new MaybeStack(es.identifier(), es.count() + stack.count(), es.patch())));
                        merged = true;
                        break;
                    }
                }
                if (merged) continue;
            }

            result.add(chanced);
        }

        return result;
    }

    public static List<List<ItemStack>> condenseInputs(List<Ingredient> ingredients)
    {
        Map<Ingredient, Integer> counts = new LinkedHashMap<>();
        for (Ingredient ingredient : ingredients)
        {
            if (ingredient.isEmpty()) continue;
            counts.merge(ingredient, 1, Integer::sum);
        }

        List<List<ItemStack>> result = new ArrayList<>(counts.size());
        counts.forEach((ingredient, count) ->
        {
            List<ItemStack> variants = new ArrayList<>();
            for (ItemStack stack : ingredient.getItems())
            {
                variants.add(stack.copyWithCount(count));
            }
            result.add(variants);
        });
        return result;
    }
}
