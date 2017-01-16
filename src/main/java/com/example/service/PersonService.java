package com.example.service;


import java.util.List;

import com.example.model.Person;
import com.example.model.PioCylinderHistory;

public interface PersonService {
    
    public void addPerson(Person person);
    public List<Person> listPeople();
    public void addHistory(PioCylinderHistory history);
    public List<PioCylinderHistory> listHistory();
    public void removePerson(Integer id);
}
