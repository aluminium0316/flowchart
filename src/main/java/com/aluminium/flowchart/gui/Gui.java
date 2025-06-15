package com.aluminium.flowchart.gui;

import com.aluminium.flowchart.Flowchart;
import com.aluminium.flowchart.Tags;
import com.aluminium.flowchart.gui.tools.MoveTool;
import com.aluminium.flowchart.gui.tools.Tools;
import com.aluminium.flowchart.utils.Arrow;
import com.aluminium.flowchart.utils.Node;
import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.drawable.text.StringKey;
import com.cleanroommc.modularui.factory.ClientGUI;
import com.cleanroommc.modularui.screen.*;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.FluidSlotSyncHandler;
import com.cleanroommc.modularui.widget.EmptyWidget;
import com.cleanroommc.modularui.widget.ScrollWidget;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widget.WidgetTree;
import com.cleanroommc.modularui.widget.scroll.HorizontalScrollData;
import com.cleanroommc.modularui.widget.scroll.VerticalScrollData;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.TextWidget;
import com.cleanroommc.modularui.widgets.slot.FluidSlot;
import com.cleanroommc.modularui.widgets.slot.PhantomItemSlot;
import com.github.vfyjxf.jeiutilities.jei.ingredient.RecipeInfo;
import com.google.common.collect.ImmutableList;
import mezz.jei.Internal;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.bookmarks.BookmarkList;
import mezz.jei.ingredients.Ingredients;
import mezz.jei.recipes.RecipeMap;
import mezz.jei.recipes.RecipeRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//@SideOnly(Side.CLIENT)
public class Gui extends CustomModularScreen {

    private int recipes = 0;
//    private static int ITEMSIZE = 16;
    private static ItemStack[] outputPreview;
    private static FluidStack[] outputPreview2;
    private static Ingredients[] ingredients;
    private static ArrayList<Node> nodes;
    private static ArrayList<Arrow> arrows;
    public static Tools tool;

    public static void open() {
        UISettings settings = new UISettings();
        settings.getJeiSettings().enableJei();
        settings.customContainer(ModularContainer::new);

        ClientGUI.open(new Gui(), settings);
    }

    @Override
    public ModularPanel buildUI(ModularGuiContext context) {
        if (ingredients == null) {
            ingredients = new Ingredients[256];
            Arrays.fill(ingredients, new Ingredients());
        }
        if (outputPreview == null) {
            outputPreview = new ItemStack[ingredients.length];
        }
        if (outputPreview2 == null) {
            outputPreview2 = new FluidStack[ingredients.length];
        }
        if (nodes == null) {
            nodes = new ArrayList<>();
        }
        if (tool == null) {
            tool = new Tools();
            tool.enabled = Tools.EnabledTool.MOVETOOL;
        }
        if (arrows == null) {
            arrows = new ArrayList<>();
        }

        IWidget center = new Widget<>()
                .pos(0, 0);

        EditorWidget node = new EditorWidget() {
            @Override
            public void afterInit() {
                super.afterInit();
                tool.ctx = center.getContext();
            }
        }
                .size(600, 384)
                .pos(32, 24)
                .keepScrollBarInArea()
                .background(UITexture.builder()
                        .location(Tags.MOD_ID, "background")
                        .imageSize(24, 24)
                        .tiled()
                        .uv(0, 0, 24, 24)
                        .name("tiled_graph_background")
                        .build());

//        node.getScrollArea().setScrollData(new HorizontalScrollData());
//        node.getScrollArea().setScrollData(new VerticalScrollData());
//        node.getScrollArea().getScrollX().setScrollSize();
        MoveTool.IPosition mouse = new MoveTool.Position(0, 0) {
            @Override
            public MoveTool.Position getPos() {
                return new MoveTool.Position(center.getContext().getMouseX(), center.getContext().getMouseY());
            }
        };

        Line line = new Line(tool.selection, mouse);
        tool.line = line;
        tool.node = node;

        renderNodes(node, line);
        renderArrows(node);

        node.child(center);

        node.child(new IDrawable.DrawableWidget(line)
                .size(64, 64));

//        ScrollWidget<?> w1 = null;
        ScrollWidget<?> w1 =  new ScrollWidget<>(new VerticalScrollData())
                .size(24, 448)
                .pos(8, 24)
                .hoverBackground(GuiTextures.SLOT_ITEM)
                .keepScrollBarInArea();
        w1.child(new ButtonWidget<>()
                .onMousePressed(mouseButton -> {
                    if (mouseButton == 0 | mouseButton == 1) {
                        getBookmarks();
                        Interactable.playButtonClickSound();
//                        WidgetTree.resize(w1);
                        open();
                    }
                    return false;
                }));
        w1.getScrollArea().getScrollY().setScrollSize(18*258);

        for (int i = 0; i < ingredients.length; i++) {
            Ingredients ingredient = ingredients[i];

            if (outputPreview[i] != null) {
                ItemStackHandler handler = new ItemStackHandler();
                handler.setStackInSlot(0, outputPreview[i]);

                w1.child(new PhantomItemSlot() {
                    @Override
                    public Result onMousePressed(int mouseButton) {
                        Interactable.playButtonClickSound();
                        nodes.add(uningredient(ingredient));
                        node.getChildren().removeIf(child -> child instanceof NodeWidget);
                        renderNodes(node, line);
                        WidgetTree.resize(node);
                        return Result.SUCCESS;
                    }

                    @Override
                    public boolean onMouseScroll(UpOrDown scrollDirection, int amount) {
                        return false;
                    }
                }
                        .slot(handler, 0)
                        .pos(0, 18 + 18 * i));
            }
            else if (outputPreview2[i] != null) {
                FluidSlotSyncHandler handler = new FluidSlotSyncHandler(new FluidTank(outputPreview2[i], 1));
                handler.phantom(true);
                handler.updateCacheFromSource(true);

                w1.child(new FluidSlot() {
                    @Override
                    public Result onMousePressed(int mouseButton) {
                        Interactable.playButtonClickSound();
                        nodes.add(uningredient(ingredient));
                        node.getChildren().removeIf(child -> child instanceof NodeWidget);
                        renderNodes(node, line);
                        WidgetTree.resize(node);
                        return Result.SUCCESS;
                    }

                    @Override
                    public boolean onMouseScroll(UpOrDown scrollDirection, int amount) {
                        return false;
                    }
                }
                        .syncHandler(handler)
                        .pos(0, 18 + 18 * i));
            }
        }

        ScrollWidget<?> tools = new ScrollWidget<>()
                .pos(32, 408)
                .size(600, 64)
                .child(new ButtonWidget<>()
                        .onMousePressed(button -> {
                            tool.enabled = Tools.EnabledTool.MOVETOOL;
                            Interactable.playButtonClickSound();
                            return true;
                        })
                        .pos(0, 0)
                        .size(48, 12)
                        .child(new TextWidget(IKey.str("move"))
                                .pos(0, 0)))
                .child(new ButtonWidget<>()
                        .onMousePressed(button -> {
                            tool.enabled = Tools.EnabledTool.ARROWTOOL;
                            Interactable.playButtonClickSound();
                            return true;
                        })
                        .pos(48, 0)
                        .size(48, 12)
                        .child(new TextWidget(IKey.str("arrow"))
                                .pos(0, 0)));

//        Flowchart.LOGGER.info(ReflectionToStringBuilder.toString(ingredients));

        ModularPanel panel = ModularPanel.defaultPanel("flowchart_panel_2", 640, 480);
        panel.child(IKey.str("Flowchart, a: " + recipes).asWidget()
                        .top(7).left(7))
                .child(w1)
                .child(node)
                .child(tools);

        return panel;
    }

    private void renderNodes(ScrollWidget<?> nodeWidget, Line line) {
        for (Node node : nodes) {
            NodeWidget widget = new NodeWidget(new VerticalScrollData(), i -> {
                tool.select(node, arrows, i.i);
                renderArrows(nodeWidget);
                WidgetTree.resize(nodeWidget);
//                Flowchart.LOGGER.info(ReflectionToStringBuilder.toString(line));
                }, node)
                    .pos(node.x, node.y)
                    .size(24, 24)
                    .background(GuiTextures.DISPLAY_SMALL)
                    .keepScrollBarInArea();
            node.widget = widget;

            nodeWidget.child(widget);
        }
    }

    private void renderArrows(ScrollWidget<?> nodeWidget) {
        for (Arrow arrow : arrows) {
            ArrowWidget widget = new ArrowWidget(arrow)
                    .pos(0, 0)
                    .size(24, 24);
//            node.widget = widget;

            nodeWidget.child(widget);
        }
    }

    private Node uningredient(Ingredients ingredient) {
        List<List<ItemStack>> inputItem = new ArrayList<>(ingredient.getInputs(VanillaTypes.ITEM));
        List<List<FluidStack>> inputFluid = new ArrayList<>(ingredient.getInputs(VanillaTypes.FLUID));
        List<List<ItemStack>> outputItem = new ArrayList<>(ingredient.getOutputs(VanillaTypes.ITEM));
        List<List<FluidStack>> outputFluid = new ArrayList<>(ingredient.getOutputs(VanillaTypes.FLUID));
        return new Node(inputItem, inputFluid, outputItem, outputFluid);
    }

    private void getBookmarks() {
        BookmarkList bookmark = Internal.getBookmarkList();
        int i = 0;
        int j = 0;
        try {
            Field field = BookmarkList.class.getDeclaredField("list");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<Object> recipes = (List<Object>) field.get(bookmark);
            for (Object recipe_ : recipes) {
                if (recipe_ instanceof RecipeInfo) {
                    RecipeInfo<?, ?> recipe = (RecipeInfo<?, ?>) recipe_;
//                    recipe.getIngredient();
                    Object ingredient = recipe.getIngredient();
                    Object result = recipe.getResult();

//                    Flowchart.LOGGER.info(recipe.toString());

                    if (Internal.getRuntime() != null) {
                        RecipeRegistry registry = Internal.getRuntime().getRecipeRegistry();
                        IRecipeCategory<?> category = registry.getRecipeCategory(recipe.getRecipeCategoryUid());
                        Field output = RecipeRegistry.class.getDeclaredField("recipeOutputMap");
                        output.setAccessible(true);
                        RecipeMap outputMap = (RecipeMap) output.get(registry);
                        if (category != null) {
                            @SuppressWarnings("unchecked")
                            ImmutableList<IRecipeWrapper> recipes2 = (ImmutableList<IRecipeWrapper>) outputMap.getRecipeWrappers(category, ingredient);


                            IRecipeWrapper recipeWrapper = recipes2.get(recipe.getRecipeIndex());
                            Ingredients ingredients = new Ingredients();
                            recipeWrapper.getIngredients(ingredients);

                            Gui.ingredients[j] = ingredients;
                            if (result instanceof ItemStack) {
                                outputPreview[j] = (ItemStack) result;
                                outputPreview2[j] = null;
                            }
                            else {
                                outputPreview[j] = null;
                                outputPreview2[j] = (FluidStack) result;
                            }
                            j += 1;
                        }
                    }

//                    if (ingredient instanceof ItemStack) {
//                        items[i] = (ItemStack) ingredient;
//                        i += 1;
//                    }
//                    if (result instanceof ItemStack) {
//                        items[i] = (ItemStack) result;
//                        i += 1;
//                    }
                }
            }
        } catch (Exception e) {
            Flowchart.LOGGER.error(e, e);
        }
    }
}
