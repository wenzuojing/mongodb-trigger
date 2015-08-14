package org.wzj.mongodb.trigger;

/**
 * Created by wens on 15/8/13.
 */
public enum Operation {

    INSERT("i"), UPDATE("u"), DELETE("d"), DROP_COLLECTION("dc"), DROP_DATABASE("dd"), COMMAND("c"), UNKNOWN(null);

    private String value;

    private Operation(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Operation fromString(String value) {
        if (value != null) {
            for (Operation operation : Operation.values()) {
                if (value.equalsIgnoreCase(operation.getValue())) {
                    return operation;
                }
            }

        }
        return Operation.UNKNOWN;
    }

}
