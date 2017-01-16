package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.model.Person;
import com.example.model.PioCylinderHistory;
import com.example.service.PersonService;

import java.util.Map;

@Controller
public class PersonController {

    @Autowired
    private PersonService personService;

    @RequestMapping("/")
    public String listPeople(Map<String, Object> map) {

        map.put("person", new Person());
        map.put("peopleList", personService.listPeople());

        return "people";
    }
    
    @RequestMapping("/history")
    public String listHistory(Map<String, Object> map) {

        map.put("pioCylinderHistory", new PioCylinderHistory());
        map.put("historyList", personService.listHistory());

        return "history";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addPerson(@ModelAttribute("person") Person person, BindingResult result) {

        personService.addPerson(person);

        return "redirect:/people/";
    }
    @RequestMapping(value = "/addHistory", method = RequestMethod.POST)
    public String addHistory(@ModelAttribute("pioCylinderHistory") PioCylinderHistory history, BindingResult result) {

        personService.addHistory(history);

        return "redirect:/people/history";
    }

    @RequestMapping("/delete/{personId}")
    public String deletePerson(@PathVariable("personId") Integer personId) {

        personService.removePerson(personId);

        return "redirect:/people/";
    }
}
