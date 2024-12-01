package org.pom.utils;

import lombok.extern.slf4j.Slf4j;
import org.pom.Constants;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class MessagesUtil {
    public static <T> String addParametersMessage(String description, Map<String, T> params) {
        return params.keySet().stream().map(
                key -> String.format(Constants.KEY_VALUE_MESSAGE, key, params.get(key))
        ).collect(Collectors.joining("; ", description, "."));
    }
}
