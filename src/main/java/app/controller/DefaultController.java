package app.controller;

import app.model.Data;
import app.repository.DataDAO;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class DefaultController {

    @Autowired
    private DataDAO dataDAO;

    @GetMapping("")
    public String basePage() {
        return "index";
    }
    @RequestMapping("/sexchartdata")
    @ResponseBody
    public String getSexDataFromDB(){
        JsonArray jsonArrayCategory = new JsonArray();
        JsonArray jsonArraySeries = new JsonArray();
        JsonObject jsonObject = new JsonObject();

        jsonArrayCategory.add("Female");
        jsonArrayCategory.add("Male");
        jsonArrayCategory.add("Other");
        jsonObject.add("categories", jsonArrayCategory);

        int female = dataDAO.getPopNo("Female");
        int male = dataDAO.getPopNo("Male");
        int other = dataDAO.getPopNo("Other");
        jsonArraySeries.add(female);
        jsonArraySeries.add(male);
        jsonArraySeries.add(other);
        jsonObject.add("series", jsonArraySeries);

        return jsonObject.toString();
    }

    @RequestMapping("/nationchartdata")
    @ResponseBody
    public String getNationDataFromDB(){
        JsonArray jsonArrayCategory = new JsonArray();
        JsonArray jsonArraySeries = new JsonArray();
        JsonObject jsonObject = new JsonObject();

        List<Data> dataList = dataDAO.findAll();
        Set<String> nationalities = new HashSet<>();
        dataList.forEach(data->{
            nationalities.add(data.getNationality());
        });
        for (String s : nationalities){
            jsonArrayCategory.add(s);
            jsonArraySeries.add(dataDAO.getByNationality(s));
        }
        jsonObject.add("numbers", jsonArraySeries);
        jsonObject.add("nations", jsonArrayCategory);
        return jsonObject.toString();
    }

    @RequestMapping("/agechartdata")
    @ResponseBody
    public String getAgeDataFromDB(){
        JsonArray jsonArrayCategory = new JsonArray();
        JsonArray jsonArraySeries = new JsonArray();
        JsonObject jsonObject = new JsonObject();

        List<Data> dataList = dataDAO.findAll();
        Set<String> dobs = new HashSet<>();
        dataList.forEach(data->{
            dobs.add(data.getDob());
        });

        for (String s : dobs){
            jsonArrayCategory.add(s.substring(s.length()-4));
            jsonArraySeries.add(dataDAO.getByDob(s));
        }
        jsonObject.add("numbers", jsonArraySeries);
        jsonObject.add("age", jsonArrayCategory);
        return jsonObject.toString();
    }


    @GetMapping("/403")
    public String accessDenied() {
        return "403";
    }
}
