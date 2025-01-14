package javaweb.task_management_system.serializers;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import javaweb.task_management_system.exceptions.InvalidValueException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {
    //MM-yyyy -> yyyy-MM-dd
    String dateRegex = "^(0[1-9]|1[0-2])-(19|20)\\d{2}$";
    private static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @Override
    public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String dateStr = jsonParser.getText();
        if(!dateStr.matches(dateRegex))
            throw new InvalidValueException("Invalid data format!");
        String[] dateParts = dateStr.split("-");
        String newDateStr = dateParts[1] + "-" + dateParts[0] + "-01";
        return LocalDate.parse(newDateStr, OUTPUT_FORMATTER);


    }
}
