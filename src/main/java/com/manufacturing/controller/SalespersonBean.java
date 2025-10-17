package com.manufacturing.controller;

import com.manufacturing.model.Salesperson;
import com.manufacturing.service.SalespersonService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Component
@ViewScoped
public class SalespersonBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Autowired
    private SalespersonService salespersonService;

    private List<Salesperson> salespersons;
    private Salesperson selectedSalesperson;
    private Salesperson salesperson;

    @PostConstruct
    public void init() {
        loadSalespersons();
        salesperson = new Salesperson();
    }

    public void loadSalespersons() {
        salespersons = salespersonService.findAll();
    }

    public void openNew() {
        salesperson = new Salesperson();
        salesperson.setActive(true);
        salesperson.setHireDate(LocalDate.now());
    }

    public void saveSalesperson() {
        try {
            salespersonService.save(salesperson);
            loadSalespersons();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Salesperson saved successfully"));
            salesperson = new Salesperson();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to save salesperson: " + e.getMessage()));
        }
    }

    public void deleteSalesperson() {
        try {
            salespersonService.delete(selectedSalesperson.getId());
            salespersons.remove(selectedSalesperson);
            selectedSalesperson = null;
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Salesperson deleted successfully"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to delete salesperson: " + e.getMessage()));
        }
    }

    // Getters and Setters
    public List<Salesperson> getSalespersons() {
        return salespersons;
    }

    public void setSalespersons(List<Salesperson> salespersons) {
        this.salespersons = salespersons;
    }

    public Salesperson getSelectedSalesperson() {
        return selectedSalesperson;
    }

    public void setSelectedSalesperson(Salesperson selectedSalesperson) {
        this.selectedSalesperson = selectedSalesperson;
    }

    public Salesperson getSalesperson() {
        return salesperson;
    }

    public void setSalesperson(Salesperson salesperson) {
        this.salesperson = salesperson;
    }
}