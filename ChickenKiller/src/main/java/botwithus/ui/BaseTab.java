package botwithus.ui;

import net.botwithus.imgui.ImGui;

/**
 * Base class for all UI tabs in the GUI.
 * Provides common functionality and structure for tab implementations.
 */
public class BaseTab {
    protected String searchText = "";

    /**
     * Constructor for the base tab.
     */
    public BaseTab() {
        // Base constructor
    }

    /**
     * Render method that tab implementations can override.
     * This handles the UI rendering for the tab.
     */
    public void render() {
        // Default implementation - override in subclasses
    }

    /**
     * Helper method to render a search box.
     * @param label The label for the search input
     * @return The current search text
     */
    protected String renderSearchBox(String label) {
        ImGui.inputText(label, searchText, 256);
        onSearchTextChanged(searchText);
        return searchText;
    }

    /**
     * Helper method to render a progress indicator.
     * @param loaded The number of items loaded
     * @param total The total number of items to load
     */
    protected void renderProgress(int loaded, int total) {
        if (loaded < total) {
            ImGui.text("Loading... (" + loaded + "/" + total + ")");

            // Add a progress bar
            float progress = total > 0 ? (float) loaded / total : 0.0f;
            ImGui.progressBar(progress, -1.0f, 0.0f, loaded + "/" + total);
        }
    }

    /**
     * Helper method to render a section header with separator.
     * @param title The section title
     */
    protected void renderSectionHeader(String title) {
        ImGui.text(title);
        ImGui.separator();
    }

    /**
     * Helper method to render a collapsible section.
     * @param title The section title
     * @param content The content to render when expanded
     * @param defaultOpen Whether the section should be open by default
     */
    protected void renderCollapsibleSection(String title, Runnable content, boolean defaultOpen) {
        if (ImGui.collapsingHeader(title, defaultOpen ? 1 : 0)) {
            content.run();
        }
    }

    /**
     * Helper method to render a collapsible section (closed by default).
     * @param title The section title
     * @param content The content to render when expanded
     */
    protected void renderCollapsibleSection(String title, Runnable content) {
        renderCollapsibleSection(title, content, false);
    }

    /**
     * Helper method to render a button with action.
     * @param text The button text
     * @param action The action to execute when clicked
     * @return true if the button was clicked
     */
    protected boolean renderButton(String text, Runnable action) {
        if (ImGui.button(text, 0, 0)) {
            action.run();
            return true;
        }
        return false;
    }

    /**
     * Helper method to render a button with custom size.
     * @param text The button text
     * @param width Button width
     * @param height Button height
     * @param action The action to execute when clicked
     * @return true if the button was clicked
     */
    protected boolean renderButton(String text, float width, float height, Runnable action) {
        if (ImGui.button(text, width, height)) {
            action.run();
            return true;
        }
        return false;
    }

    /**
     * Helper method to render a checkbox.
     * @param label The checkbox label
     * @param value The current value
     * @param onChange Callback when value changes
     * @return the new checkbox value
     */
    protected boolean renderCheckbox(String label, boolean value, Runnable onChange) {
        if (ImGui.checkbox(label, value)) {
            onChange.run();
            return !value;
        }
        return value;
    }

    /**
     * Helper method to render an integer slider.
     * @param label The slider label
     * @param value Current value
     * @param min Minimum value
     * @param max Maximum value
     * @param onChange Callback when value changes
     * @return the new value
     */
    protected int renderSliderInt(String label, int value, int min, int max, Runnable onChange) {
        ImGui.sliderInt(label, value, min, max, "", 1);
        onChange.run();
        return value;
    }

    /**
     * Helper method to render a float slider.
     * @param label The slider label
     * @param value Current value
     * @param min Minimum value
     * @param max Maximum value
     * @param onChange Callback when value changes
     * @return the new value
     */
    protected float renderSliderFloat(String label, float value, float min, float max, Runnable onChange) {
        ImGui.sliderFloat(label, value, min, max, "", 1);
        onChange.run();
        return value;
    }

    /**
     * Helper method to add spacing between elements.
     */
    protected void addSpacing() {
        ImGui.spacing();
    }

    /**
     * Helper method to add elements on the same line.
     */
    protected void sameLine() {
        ImGui.sameLine(0, 0);
    }

    /**
     * Callback method that can be overridden to handle search text changes.
     * @param newSearchText The new search text
     */
    protected void onSearchTextChanged(String newSearchText) {
        // Override in subclasses to handle search text changes
    }
}
