package scanmycar.model.service;

import scanmycar.model.repository.*;
import scanmycar.model.dto.*;

import java.util.List;
import java.util.Optional;

/**
 * A service class that centralizes access to all data repositories.
 * It provides CRUD operations for Vehicle, Owner, Agent, and Inspection entities.
 * This class acts as a middle layer between the business logic and the repositories.
 */
public class DataServiceImpl {

    private final VehicleRepository vehicleRepository;
    private final OwnerRepository ownerRepository;
    private final AgentRepository agentRepository;
    private final InspectionRepository inspectionRepository;

    /**
     * Default constructor initializing all repositories with new instances.
     */
    public DataServiceImpl() {
        this.vehicleRepository = new VehicleRepository();
        this.ownerRepository = new OwnerRepository();
        this.agentRepository = new AgentRepository();
        this.inspectionRepository = new InspectionRepository();
    }

    /**
     * Constructor for injecting repository dependencies, useful for testing.
     */
    public DataServiceImpl(
            VehicleRepository vehicleRepository,
            OwnerRepository ownerRepository,
            AgentRepository agentRepository,
            InspectionRepository inspectionRepository
    ) {
        this.vehicleRepository = vehicleRepository;
        this.ownerRepository = ownerRepository;
        this.agentRepository = agentRepository;
        this.inspectionRepository = inspectionRepository;
    }

    // VEHICLE METHODS

    /**
     * Finds a vehicle by its ID.
     */
    public Optional<VehicleDto> findVehicleById(int id) {
        return vehicleRepository.findById(id);
    }

    /**
     * Retrieves all vehicles.
     */
    public List<VehicleDto> findAllVehicles() {
        return vehicleRepository.findAll();
    }

    /**
     * Retrieves all vehicle license plates.
     */
    public List<String> findAllPlates() {
        return vehicleRepository.findAllPlate();
    }

    /**
     * Finds a vehicle by its license plate.
     */
    public Optional<VehicleDto> findVehicleByPlate(String plate) {
        return vehicleRepository.findByPlate(plate);
    }

    /**
     * Saves a vehicle.
     */
    public Integer saveVehicle(VehicleDto vehicle) {
        return vehicleRepository.save(vehicle);
    }

    /**
     * Deletes a vehicle by its ID.
     */
    public void deleteVehicleById(int id) {
        vehicleRepository.deleteById(id);
    }

    // OWNER METHODS

    /**
     * Finds an owner by their ID.
     */
    public Optional<OwnerDto> findOwnerById(int id) {
        return ownerRepository.findById(id);
    }

    /**
     * Retrieves all owners.
     */
    public List<OwnerDto> findAllOwners() {
        return ownerRepository.findAll();
    }

    /**
     * Saves an owner.
     */
    public Integer saveOwner(OwnerDto owner) {
        return ownerRepository.save(owner);
    }

    /**
     * Deletes an owner by their ID.
     */
    public void deleteOwnerById(int id) {
        ownerRepository.deleteById(id);
    }

    /**
     * Retrieves all registered owner emails.
     */
    public List<String> getAllEmailsOwner() {
        return ownerRepository.getAllEmails();
    }

    /**
     * Finds an owner's ID by their email.
     */
    public Optional<Integer> findOwnerByEmail(String email){
        return ownerRepository.findByEmail(email);
    }

    // AGENT METHODS

    /**
     * Finds an agent by their ID.
     */
    public Optional<AgentDto> findAgentById(int id) {
        return agentRepository.findById(id);
    }

    /**
     * Retrieves all agents.
     */
    public List<AgentDto> findAllAgents() {
        return agentRepository.findAll();
    }

    /**
     * Saves an agent.
     */
    public Integer saveAgents(AgentDto agent) {
        return agentRepository.save(agent);
    }

    /**
     * Deletes an agent by their ID.
     */
    public void deleteAgentById(int id) {
        agentRepository.deleteById(id);
    }

    // INSPECTION METHODS

    /**
     * Finds an inspection by its ID.
     */
    public Optional<InspectionDto> findInspectionById(int id) {
        return inspectionRepository.findById(id);
    }

    /**
     * Retrieves all inspections.
     */
    public List<InspectionDto> findAllInspections() {
        return inspectionRepository.findAll();
    }

    /**
     * Saves an inspection.
     */
    public Integer saveInspection(InspectionDto inspection) {
        return inspectionRepository.save(inspection);
    }

    /**
     * Deletes an inspection by its ID.
     */
    public void deleteInspectionById(int id) {
        inspectionRepository.deleteById(id);
    }

    /**
     * Retrieves all inspections related to a specific vehicle plate.
     */
    public List<InspectionDto> findInspectionByPlate(String licensePlate) {
        return inspectionRepository.findByPlate(licensePlate);
    }

}
