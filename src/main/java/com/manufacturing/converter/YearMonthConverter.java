package com.manufacturing.converter;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@FacesConverter("yearMonthConverter")
public class YearMonthConverter implements Converter<YearMonth> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Override
    public YearMonth getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            // Remove any extra characters and parse
            String cleanValue = value.trim();
            return YearMonth.parse(cleanValue, FORMATTER);
        } catch (DateTimeParseException e) {
            // If parsing fails, return current month
            System.err.println("Error parsing YearMonth: " + value + " - " + e.getMessage());
            return YearMonth.now();
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, YearMonth value) {
        if (value == null) {
            return "";
        }

        try {
            return value.format(FORMATTER);
        } catch (Exception e) {
            System.err.println("Error formatting YearMonth: " + e.getMessage());
            return "";
        }
    }
}