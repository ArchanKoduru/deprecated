package com.gourmetoven.gourmetapp.util;

import java.util.HashMap;
import java.util.Map;

public class TypeMapper {

    private TypeMapper(){}
    public static Map<Integer,String> typeMap = new HashMap<Integer,String>() {{
        put(1, "vegetarian");
        put(2, "non-vegetarian");
        put(3, "ingredient");
        put(4, "recipe");
        put(5, "user-defined");
        put(6, "default");
    }};

    public static Map<String,Integer> typeToIndexMap = new HashMap<String,Integer>() {{
        put("vegetarian",1);
        put("non-vegetarian",2);
        put("ingredient",3);
        put("recipe",4);
        put("user-defined",5);
        put("default",6);
    }};

    public static String getType(Integer type)
    {
        return typeMap.get(type);
    }
}
