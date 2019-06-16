package sample;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Test {

    public static void main(String[] args) {
        HashMap<String, String> my_map = new HashMap<String, String>();

        my_map.put("wplx", "some commmand");
        my_map.put("buttons", "other commands");

        Gson gson = new Gson();
        String json_x = (gson.toJson(my_map));

        HashMap o = gson.fromJson(json_x, HashMap.class);

        Iterator i = o.entrySet().iterator();

        while (i.hasNext())
        {
            Map.Entry pair = (Map.Entry) i.next();
            System.out.println(pair.getKey());
        }
    }
}
