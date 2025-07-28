package botwithus.state;

public enum BotState {
    FLAX_PICKING("Flax Picking"),
    BANKING("Banking");

    private final String description;

    BotState(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
