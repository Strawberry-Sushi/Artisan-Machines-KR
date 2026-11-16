package com.wdiscute.artisan.compat.jei;

import com.wdiscute.artisan.Artisan;
import com.wdiscute.artisan.ChancedStack;
import com.wdiscute.artisan.recipe.AbstractArtisanRecipe;
import com.wdiscute.artisan.registry.ArtisanBlocks;
import com.wdiscute.utils.ScreenUtils;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.List;

public class ArtisanJeiDehydratorRecipe extends AbstractRecipeCategory<ArtisanJeiDehydratorRecipe.Recipe>
{
    final ItemStack clock;
    public static final RecipeType<Recipe> TYPE = new RecipeType<>(Artisan.rl("dehydrator"), Recipe.class);

    public ArtisanJeiDehydratorRecipe(IGuiHelper guiHelper, DeferredBlock<Block> block)
    {
        super(
                TYPE,
                Component.translatable("emi.category.artisan." + block.getId().getPath()),
                guiHelper.createDrawableItemLike(block),
                160,
                40
        );

        clock = Items.CLOCK.getDefaultInstance();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, Recipe recipe, IFocusGroup focuses)
    {
        for (int i = 0; i < recipe.input.size(); i++)
        {
            if (i == 0)
                builder.addInputSlot(2, 2)
                        .addItemStacks(recipe.input.get(i))
                        .setStandardSlotBackground();
            else
                builder.addInputSlot(2 + i * 20 - 20, 20)
                        .addItemStacks(recipe.input.get(i))
                        .setStandardSlotBackground();
        }

        builder.addInputSlot(55, 2)
                .addItemStack(ArtisanBlocks.DEHYDRATOR.get().asItem().getDefaultInstance())
                .setStandardSlotBackground();

        if (!recipe.requiredUpgrades.isEmpty())
            builder.addInputSlot(55, 20)
                    .addIngredients(recipe.requiredUpgrades)
                    .setStandardSlotBackground();

        var rStream = recipe.output.stream().map(o -> Ingredient.of(o.stack().toStack())).toArray(Ingredient[]::new);
        builder.addOutputSlot(109, 2)
                .addIngredients(CompoundIngredient.of(rStream))
                .setStandardSlotBackground();
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, Recipe recipe, IFocusGroup focuses)
    {
        super.createRecipeExtras(builder, recipe, focuses);

        builder.addRecipeArrow().setPosition(26, 1);
        builder.addRecipeArrow().setPosition(79, 1);
    }

    @Override
    public void draw(Recipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics g, double mouseX, double mouseY)
    {
        Font font = Minecraft.getInstance().font;

        int days = recipe.processing / 24;
        int hours = recipe.processing % 24;
        int x = 95;
        int y = 25;

        MutableComponent comp;

        if (days > 0)
        {
            if (hours != 0)
                comp = Component.translatable("jemi.processing_time.days_hours", days, hours);
            else
                comp = Component.translatable("jemi.processing_time.days", days);
        }
        else
        {
            comp = Component.translatable("jemi.processing_time.hours", hours);
        }

        ScreenUtils.text(g, font, comp, x, y, 0xffffffff, true);

        ScreenUtils.item(g, clock, x - 20, y + font.lineHeight / 2 - 8);
    }

    @Override
    public ResourceLocation getRegistryName(Recipe recipe)
    {
        return BuiltInRegistries.ITEM.getKey(recipe.output.getFirst().stack().toStack().getItem());
    }

    public record Recipe(int processing, List<List<ItemStack>> input, List<ChancedStack> output,
                         Ingredient requiredUpgrades)
    {
        public static Recipe of(RecipeHolder<AbstractArtisanRecipe> holder)
        {
            AbstractArtisanRecipe recipe = holder.value();

            return new Recipe(
                    recipe.processing_hours,
                    ArtisanJeiPlugin.condenseInputs(recipe.ingredients),
                    ArtisanJeiPlugin.condenseOutputs(recipe.result),
                    CompoundIngredient.of(recipe.requiresUpgrade
                            .stream()
                            .filter(i -> !i.isEmpty())
                            .toArray(Ingredient[]::new))
            );
        }
    }
}
