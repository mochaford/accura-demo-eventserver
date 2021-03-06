package com.example.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.model.Person;

import com.example.model.PioCylinderHistory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.util.List;

@Service
public class PersonServiceImpl implements PersonService {

    @PersistenceContext
    EntityManager em;
        
    @Transactional
    public void addPerson(Person person) {
        em.persist(person);
    }
    
    @Transactional
    public void addHistory(PioCylinderHistory history) {
        em.persist(history);
    }

    @Transactional
    public List<PioCylinderHistory> listHistory() {
        CriteriaQuery<PioCylinderHistory> c = em.getCriteriaBuilder().createQuery(PioCylinderHistory.class);
        c.from(PioCylinderHistory.class);
        return em.createQuery(c).getResultList();
    }
    
    @Transactional
    public List<Person> listPeople() {
        CriteriaQuery<Person> c = em.getCriteriaBuilder().createQuery(Person.class);
        c.from(Person.class);
        return em.createQuery(c).getResultList();
    }

    @Transactional
    public void removePerson(Integer id) {
        Person person = em.find(Person.class, id);
        if (null != person) {
            em.remove(person);
        }
    }
    
}
