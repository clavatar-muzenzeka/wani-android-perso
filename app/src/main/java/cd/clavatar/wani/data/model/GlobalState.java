package cd.clavatar.wani.data.model;

import java.util.ArrayList;

/* renamed from: cd.clavatar.wani.data.model.GlobalState */
public class GlobalState {
    private ArrayList<CompactStatus> carnetStatus;
    private ArrayList<CompactCarnet> carnets;
    private ArrayList<Check> checks;
    private ArrayList<Location> locations;
    private ArrayList<PaimentType> paiementTypes;
    private ArrayList<CompactPaiement> paiements;
    private ArrayList<WaniParameter> parameters;
    private ArrayList<People> peoples;
    private ArrayList<Story> storys;
    private ArrayList<CompactUser> users;
    private ArrayList<VaccinOccurence> vaccinOccurences;
    private ArrayList<Vaccin> vaccins;

    public GlobalState() {
    }

    public GlobalState(ArrayList<WaniParameter> parameters2, ArrayList<Location> locations2, ArrayList<People> peoples2, ArrayList<CompactUser> users2, ArrayList<PaimentType> paiementTypes2, ArrayList<CompactPaiement> paiements2, ArrayList<CompactStatus> carnetStatus2, ArrayList<Check> checks2, ArrayList<Vaccin> vaccins2, ArrayList<VaccinOccurence> vaccinOccurences2, ArrayList<Story> storys2, ArrayList<CompactCarnet> carnets2) {
        this.parameters = parameters2;
        this.locations = locations2;
        this.peoples = peoples2;
        this.users = users2;
        this.paiementTypes = paiementTypes2;
        this.paiements = paiements2;
        this.carnetStatus = carnetStatus2;
        this.checks = checks2;
        this.vaccins = vaccins2;
        this.vaccinOccurences = vaccinOccurences2;
        this.storys = storys2;
        this.carnets = carnets2;
    }

    public ArrayList<WaniParameter> getParameters() {
        return this.parameters;
    }

    public void setParameters(ArrayList<WaniParameter> parameters2) {
        this.parameters = parameters2;
    }

    public ArrayList<Location> getLocations() {
        return this.locations;
    }

    public void setLocations(ArrayList<Location> locations2) {
        this.locations = locations2;
    }

    public ArrayList<People> getPeoples() {
        return this.peoples;
    }

    public void setPeoples(ArrayList<People> peoples2) {
        this.peoples = peoples2;
    }

    public ArrayList<CompactUser> getUsers() {
        return this.users;
    }

    public void setUsers(ArrayList<CompactUser> users2) {
        this.users = users2;
    }

    public ArrayList<PaimentType> getPaiementTypes() {
        return this.paiementTypes;
    }

    public void setPaiementTypes(ArrayList<PaimentType> paiementTypes2) {
        this.paiementTypes = paiementTypes2;
    }

    public ArrayList<CompactPaiement> getPaiements() {
        return this.paiements;
    }

    public void setPaiements(ArrayList<CompactPaiement> paiements2) {
        this.paiements = paiements2;
    }

    public ArrayList<CompactStatus> getCarnetStatus() {
        return this.carnetStatus;
    }

    public void setCarnetStatus(ArrayList<CompactStatus> carnetStatus2) {
        this.carnetStatus = carnetStatus2;
    }

    public ArrayList<Check> getChecks() {
        return this.checks;
    }

    public void setChecks(ArrayList<Check> checks2) {
        this.checks = checks2;
    }

    public ArrayList<Vaccin> getVaccins() {
        return this.vaccins;
    }

    public void setVaccins(ArrayList<Vaccin> vaccins2) {
        this.vaccins = vaccins2;
    }

    public ArrayList<VaccinOccurence> getVaccinOccurences() {
        return this.vaccinOccurences;
    }

    public void setVaccinOccurences(ArrayList<VaccinOccurence> vaccinOccurences2) {
        this.vaccinOccurences = vaccinOccurences2;
    }

    public ArrayList<Story> getStorys() {
        return this.storys;
    }

    public void setStorys(ArrayList<Story> storys2) {
        this.storys = storys2;
    }

    public ArrayList<CompactCarnet> getCarnets() {
        return this.carnets;
    }

    public void setCarnets(ArrayList<CompactCarnet> carnets2) {
        this.carnets = carnets2;
    }
}
