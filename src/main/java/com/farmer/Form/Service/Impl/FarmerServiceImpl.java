package com.farmer.Form.Service.Impl;

import com.farmer.Form.DTO.FarmerDto;
import com.farmer.Form.Entity.Farmer;
import com.farmer.Form.Mapper.FarmerMapper;
import com.farmer.Form.Repository.FarmerRepository;
import com.farmer.Form.Service.FarmerService;
import com.farmer.Form.Service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FarmerServiceImpl implements FarmerService {

    @Autowired
    private FarmerRepository farmerRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private com.farmer.Form.Repository.EmployeeRepository employeeRepository;

    @Override
    public FarmerDto createFarmer(FarmerDto dto, MultipartFile photo, MultipartFile passbookPhoto,
                                  MultipartFile aadhaar, MultipartFile soilTestCertificate) {
        try {
            String photoFile = (photo != null && !photo.isEmpty())
                    ? fileStorageService.storeFile(photo, "photos") : null;
            String passbookFile = (passbookPhoto != null && !passbookPhoto.isEmpty())
                    ? fileStorageService.storeFile(passbookPhoto, "passbooks") : null;
            String aadhaarFile = (aadhaar != null && !aadhaar.isEmpty())
                    ? fileStorageService.storeFile(aadhaar, "documents") : null;
            String soilTestFile = (soilTestCertificate != null && !soilTestCertificate.isEmpty())
                    ? fileStorageService.storeFile(soilTestCertificate, "soil-tests") : null;

            Farmer farmer = FarmerMapper.toEntity(dto, photoFile, passbookFile, aadhaarFile, soilTestFile);
            Farmer saved = farmerRepository.save(farmer);
            return FarmerMapper.toDto(saved);

        } catch (IOException e) {
            throw new RuntimeException("Failed to store uploaded files", e);
        }
    }

    @Override
    public FarmerDto getFarmerById(Long id) {
        return farmerRepository.findById(id)
                .map(FarmerMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));
    }

    @Override
    public List<FarmerDto> getAllFarmers() {
        return farmerRepository.findAll().stream()
                .map(FarmerMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public FarmerDto updateFarmer(Long id, FarmerDto dto,
                                  MultipartFile photo, MultipartFile passbookPhoto,
                                  MultipartFile aadhaar, MultipartFile soilTestCertificate) {

        Farmer existing = farmerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));

        try {
            String photoFile = (photo != null && !photo.isEmpty())
                    ? fileStorageService.storeFile(photo, "photos")
                    : existing.getPhotoFileName();

            String passbookFile = (passbookPhoto != null && !passbookPhoto.isEmpty())
                    ? fileStorageService.storeFile(passbookPhoto, "passbooks")
                    : existing.getPassbookFileName();

            String aadhaarFile = (aadhaar != null && !aadhaar.isEmpty())
                    ? fileStorageService.storeFile(aadhaar, "documents")
                    : existing.getDocumentFileName();

            String soilTestFile = (soilTestCertificate != null && !soilTestCertificate.isEmpty())
                    ? fileStorageService.storeFile(soilTestCertificate, "soil-tests")
                    : existing.getSoilTestCertificateFileName();

            Farmer updated = FarmerMapper.toEntity(dto, photoFile, passbookFile, aadhaarFile, soilTestFile);
            updated.setId(existing.getId());

            Farmer saved = farmerRepository.save(updated);
            return FarmerMapper.toDto(saved);

        } catch (IOException e) {
            throw new RuntimeException("Failed to update files", e);
        }
    }

    @Override
    public FarmerDto updateFarmer(Long id, FarmerDto dto) {
        Farmer existing = farmerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));

        Farmer updated = FarmerMapper.toEntity(dto,
                existing.getPhotoFileName(),
                existing.getPassbookFileName(),
                existing.getDocumentFileName(),
                existing.getSoilTestCertificateFileName());

        updated.setId(existing.getId());
        return FarmerMapper.toDto(farmerRepository.save(updated));
    }

    @Override
    public void deleteFarmer(Long id) {
        farmerRepository.deleteById(id);
    }

    @Override
    public long getFarmerCount() {
        return farmerRepository.count();
    }

    // --- SUPER ADMIN RAW CRUD ---
    @Override
    public List<Farmer> getAllFarmersRaw() {
        return farmerRepository.findAll();
    }

    @Override
    public Farmer getFarmerRawById(Long id) {
        return farmerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Farmer not found with ID: " + id));
    }

    @Override
    public Farmer createFarmerBySuperAdmin(Farmer farmer) {
        return farmerRepository.save(farmer);
    }

    @Override
    public Farmer updateFarmerBySuperAdmin(Long id, Farmer updatedFarmer) {
        Farmer farmer = getFarmerRawById(id);
        // Update fields as needed
        farmer.setFirstName(updatedFarmer.getFirstName());
        farmer.setLastName(updatedFarmer.getLastName());
        farmer.setDateOfBirth(updatedFarmer.getDateOfBirth());
        farmer.setGender(updatedFarmer.getGender());
        farmer.setContactNumber(updatedFarmer.getContactNumber());
        farmer.setCountry(updatedFarmer.getCountry());
        farmer.setState(updatedFarmer.getState());
        farmer.setDistrict(updatedFarmer.getDistrict());
        farmer.setBlock(updatedFarmer.getBlock());
        farmer.setVillage(updatedFarmer.getVillage());
        farmer.setPincode(updatedFarmer.getPincode());
        // ... update other fields as needed
        return farmerRepository.save(farmer);
    }

    @Override
    public void deleteFarmerBySuperAdmin(Long id) {
        farmerRepository.deleteById(id);
    }

    @Override
    public void assignFarmerToEmployee(Long farmerId, Long employeeId) {
        Farmer farmer = farmerRepository.findById(farmerId)
            .orElseThrow(() -> new RuntimeException("Farmer not found"));
        com.farmer.Form.Entity.Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found"));
        farmer.setAssignedEmployee(employee);
        farmerRepository.save(farmer);
    }

    @Override
    public List<Farmer> getFarmersByEmployeeEmail(String email) {
        return farmerRepository.findByAssignedEmployee_Email(email);
    }

    @Override
    public void approveKyc(Long farmerId) {
        Farmer farmer = farmerRepository.findById(farmerId)
            .orElseThrow(() -> new RuntimeException("Farmer not found"));
        farmer.setKycApproved(true);
        farmerRepository.save(farmer);
    }
}
