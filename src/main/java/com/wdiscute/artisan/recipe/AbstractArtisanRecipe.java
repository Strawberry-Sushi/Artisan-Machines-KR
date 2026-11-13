package com.wdiscute.artisan.recipe;

import com.wdiscute.artisan.ChancedStack;
import com.wdiscute.artisan.upgrades.AbstractUpgrade;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public abstract class AbstractArtisanRecipe implements Recipe<ArtisanRecipeInput>
{
    RecipeType<?> type;
    List<Ingredient> ingredients;
    List<Ingredient> requiresUpgrade;
    List<Ingredient> blacklistedUpgrades;
    List<ChancedStack> result;
    int processing_hours;

    public AbstractArtisanRecipe(RecipeType<?> type, List<Ingredient> ingredients, List<ChancedStack> result, int processing_hours, List<Ingredient> requiredUpgrades, List<Ingredient> blacklistedUpgrades)
    {
        this.type = type;
        this.ingredients = ingredients;
        this.result = result;
        this.processing_hours = processing_hours;
        this.requiresUpgrade = requiredUpgrades;
        this.blacklistedUpgrades = blacklistedUpgrades;
    }

    @Override
    public boolean matches(ArtisanRecipeInput input, Level level)
    {
        List<Ingredient> ingLeftToCheck = new ArrayList<>(ingredients);

        //if input doesn't contain all required upgrades, return false
        boolean containsAll = requiresUpgrade.stream().allMatch(r -> input.upgrades.stream().anyMatch(r));
        if(!containsAll) return false;

        for (ItemStack item : input.items)
        {
            List<Ingredient> matchingIngredients = ingLeftToCheck.stream().filter(o -> o.test(item)).toList();
            if (matchingIngredients.isEmpty()) return false;
            ingLeftToCheck.remove(matchingIngredients.getFirst());
        }

        return ingLeftToCheck.isEmpty();
    }

    @Override
    public ItemStack assemble(ArtisanRecipeInput input, HolderLookup.Provider registries)
    {
        return this.result.getFirst().stack();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height)
    {
        return true;
    }

    public int getHours()
    {
        return processing_hours;
    }

    @Override
    public NonNullList<Ingredient> getIngredients()
    {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();
        nonnulllist.addAll(List.copyOf(this.ingredients));
        return nonnulllist;
    }

    public boolean hasBlacklistedUpgrade(List<ItemStack> upgrades)
    {
        if (blacklistedUpgrades.isEmpty()) return false;

        for (ItemStack stack : upgrades)
        {
            if (stack.isEmpty()) continue;

            for (Ingredient banned : blacklistedUpgrades)
            {
                if (banned.test(stack)) return true;
            }
        }
        return false;
    }

    //Kuhn’s augmenting-path algorithm
    public boolean upgradesMatches(List<ItemStack> upgrades)
    {
        List<Ingredient> required = requiresUpgrade;

        if (required.isEmpty()) return true;
        if (upgrades.size() < required.size()) return false;

        int[] stackOwner = new int[upgrades.size()];
        Arrays.fill(stackOwner, -1);

        for (int i = 0; i < required.size(); i++)
            if (!tryAssign(required, upgrades, i, stackOwner, new boolean[upgrades.size()]))
                return false;

        return true;
    }

    private static boolean tryAssign(List<Ingredient> required, List<ItemStack> stacks, int ingredientIdx,
                                     int[] stackOwner, boolean[] visited)
    {
        Ingredient ingredient = required.get(ingredientIdx);

        for (int s = 0; s < stacks.size(); s++)
        {
            if (visited[s]) continue;

            ItemStack stack = stacks.get(s);
            if (stack.isEmpty() || !ingredient.test(stack)) continue;

            visited[s] = true;

            if (stackOwner[s] == -1 || tryAssign(required, stacks, stackOwner[s], stackOwner, visited))
            {
                stackOwner[s] = ingredientIdx;
                return true;
            }
        }
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries)
    {
        return this.result.getFirst().stack();
    }

    public List<ChancedStack> getResult()
    {
        return this.result;
    }

    @Override
    public @NotNull RecipeType<?> getType()
    {
        return type;
    }

    public interface Factory<T extends AbstractArtisanRecipe>
    {
        T create(List<Ingredient> ingredients, List<ChancedStack> result, int processing_hours, List<Ingredient> requiredUpgrades, List<Ingredient> blacklistedUpgrades);
    }
}
