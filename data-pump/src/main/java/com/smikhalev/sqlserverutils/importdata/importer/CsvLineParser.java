package com.smikhalev.sqlserverutils.importdata.importer;

import java.util.LinkedList;
import java.util.List;

/*
 * This implementation should be faster any universal third party parser
 * and faster then regexp. Since it is not universal and specific for SQL.
 */
public class CsvLineParser {

    private enum States {
        START,
        COMMA,
        STRING_FIELD,
        FIELD,
        QUOTE_IN_STRING_FIELD
    }

    public List<String> parse(String line) {
        States state = States.START;
        List<String> results = new LinkedList<>();
        StringBuilder valueBuilder = new StringBuilder();
        for(int i = 0; i < line.length(); i++){
            char currentChar = line.charAt(i);
            switch (state) {
                case START:
                    state = processCommaState(state, valueBuilder, currentChar);
                break;

                case COMMA:
                    addNewValue(results, valueBuilder);
                    state = processCommaState(state, valueBuilder, currentChar);
                break;

                case FIELD:
                    state = processFieldState(state, valueBuilder, currentChar);
                    break;

                case STRING_FIELD:
                    state = processStringFieldState(state, valueBuilder, currentChar);
                    break;

                case QUOTE_IN_STRING_FIELD:
                    state = processQuoteInStringField(state, valueBuilder, currentChar);
                break;
            }
        }
        addNewValue(results, valueBuilder);

        if (state == States.COMMA)
            results.add(null);

        return results;
    }

    private void addNewValue(List<String> results, StringBuilder valueBuilder) {
        String newValue = valueBuilder.toString();
        if (newValue.isEmpty()) {
            results.add(null);
        }
        else {
            results.add(newValue);
            valueBuilder.setLength(0);
        }
    }

    private States processQuoteInStringField(States state, StringBuilder valueBuilder, char currentChar) {
        if (currentChar == '"') {
            state = States.STRING_FIELD;
            valueBuilder.append('"');
        }
        if (currentChar == ',') {
            state = States.COMMA;
        }

        return state;
    }

    private States processStringFieldState(States state, StringBuilder valueBuilder, char currentChar) {
        if (currentChar == '"') {
            state = States.QUOTE_IN_STRING_FIELD;
        }
        else {
            valueBuilder.append(currentChar);
        }

        return state;
    }

    private States processFieldState(States state, StringBuilder valueBuilder, char currentChar) {
        if (currentChar == ',') {
            state = States.COMMA;
        }
        else {
            valueBuilder.append(currentChar);
        }

        return state;
    }

    private States processCommaState(States state, StringBuilder valueBuilder, char currentChar) {
        if (currentChar == ',') {
            state = States.COMMA;
        }
        else if (currentChar == '"') {
            state = States.STRING_FIELD;
        }
        else {
            valueBuilder.append(currentChar);
            state = States.FIELD;
        }

        return state;
    }
}
