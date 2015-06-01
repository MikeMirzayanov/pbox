package me.pbox.site.util;

import com.codeforces.commons.text.StringUtil;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Vitaliy Kudasov (kuviman@gmail.com)
 */
public class JsonUtil {
    private static final Logger logger = Logger.getLogger(JsonUtil.class);

    public static Map<String, Object> toMap(String json) {
        JsonFactory factory = new JsonFactory();
        ObjectMapper mapper = new ObjectMapper(factory);
        TypeReference<HashMap<String,Object>> typeRef
                = new TypeReference<HashMap<String,Object>>() {};

        Map<String,Object> result;
        try {
            result = mapper.readValue(json, typeRef);
        } catch (IOException e) {
            logger.error("Can't process json `" + StringUtil.shrinkTo(json, 1000) +"`.", e);
            throw new RuntimeException("Can't process json `" + StringUtil.shrinkTo(json, 1000) +"`.", e);
        }

        return result;
    }

    public static String fromMap(Map<String, Object> map) {
        JsonFactory factory = new JsonFactory();
        ObjectMapper mapper = new ObjectMapper(factory);
        try {
            return mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            String message = "Can't convert map to json `" + StringUtil.shrinkTo(map.toString(), 1000) + "`.";
            logger.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

    public static String fromList(List<?> list) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.writeValue(out, list);
        } catch (IOException e) {
            //
        }

        final byte[] data = out.toByteArray();
        return new String(data);
//
//        JsonFactory factory = new JsonFactory();
//        ObjectMapper mapper = new ObjectMapper(factory);
//        try {
//            return mapper.writeValueAsString(list);
//        } catch (JsonProcessingException e) {
//            String message = "Can't convert map to json `" + StringUtil.shrinkTo(list.toString(), 1000) + "`.";
//            logger.error(message, e);
//            throw new RuntimeException(message, e);
//        }
    }
}
