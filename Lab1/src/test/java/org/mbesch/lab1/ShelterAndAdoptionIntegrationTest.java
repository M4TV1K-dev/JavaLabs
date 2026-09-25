package org.mbesch.lab1;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mbesch.lab1.dto.AdoptionApplicationRequestDto;
import org.mbesch.lab1.dto.AnimalHandoverRecordRequestDto;
import org.mbesch.lab1.dto.AnimalRequestDto;
import org.mbesch.lab1.dto.EnclosureRequestDto;
import org.mbesch.lab1.dto.UserRequestDto;
import org.mbesch.lab1.repository.AdoptionApplicationRepository;
import org.mbesch.lab1.repository.AnimalHandoverRecordRepository;
import org.mbesch.lab1.repository.AnimalRepository;
import org.mbesch.lab1.repository.EnclosureRepository;
import org.mbesch.lab1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class ShelterAndAdoptionIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private EnclosureRepository enclosureRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdoptionApplicationRepository adoptionApplicationRepository;

    @Autowired
    private AnimalHandoverRecordRepository animalHandoverRecordRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        animalHandoverRecordRepository.clear();
        adoptionApplicationRepository.clear();
        animalRepository.clear();
        enclosureRepository.clear();
        userRepository.clear();
    }

    @Test
    void testAnimalCrud() throws Exception {
        // 1. POST /api/animals -> 201 Created
        AnimalRequestDto createDto = new AnimalRequestDto("Барсик", "Кот", 3, "IN_SHELTER", null);
        MvcResult createResult = mockMvc.perform(post("/api/animals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("Барсик")))
                .andExpect(jsonPath("$.species", is("Кот")))
                .andExpect(jsonPath("$.age", is(3)))
                .andExpect(jsonPath("$.placementStatus", is("IN_SHELTER")))
                .andReturn();

        JsonNode createdNode = objectMapper.readTree(createResult.getResponse().getContentAsString());
        long animalId = createdNode.get("id").asLong();

        // 2. GET /api/animals -> 200 OK (array)
        mockMvc.perform(get("/api/animals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Барсик")));

        // 3. GET /api/animals/{id} -> 200 OK
        mockMvc.perform(get("/api/animals/" + animalId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) animalId)))
                .andExpect(jsonPath("$.name", is("Барсик")));

        // 4. PATCH /api/animals/{id} -> 200 OK
        AnimalRequestDto patchDto = new AnimalRequestDto();
        patchDto.setAge(4);
        patchDto.setPlacementStatus("QUARANTINE");
        mockMvc.perform(patch("/api/animals/" + animalId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) animalId)))
                .andExpect(jsonPath("$.name", is("Барсик")))
                .andExpect(jsonPath("$.age", is(4)))
                .andExpect(jsonPath("$.placementStatus", is("QUARANTINE")));

        // 5. DELETE /api/animals/{id} -> 204 No Content
        mockMvc.perform(delete("/api/animals/" + animalId))
                .andExpect(status().isNoContent());

        // Verify deleted
        mockMvc.perform(get("/api/animals/" + animalId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testEnclosureCrud() throws Exception {
        // 1. POST /api/enclosures -> 201 Created
        EnclosureRequestDto createDto = new EnclosureRequestDto("Вольер для кошек №1", List.of("Кот", "Кошка"), 5);
        MvcResult createResult = mockMvc.perform(post("/api/enclosures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("Вольер для кошек №1")))
                .andExpect(jsonPath("$.capacity", is(5)))
                .andExpect(jsonPath("$.allowedSpecies", hasSize(2)))
                .andReturn();

        JsonNode createdNode = objectMapper.readTree(createResult.getResponse().getContentAsString());
        long enclosureId = createdNode.get("id").asLong();

        // 2. GET /api/enclosures -> 200 OK
        mockMvc.perform(get("/api/enclosures"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        // 3. GET /api/enclosures/{id} -> 200 OK
        mockMvc.perform(get("/api/enclosures/" + enclosureId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) enclosureId)))
                .andExpect(jsonPath("$.capacity", is(5)));

        // 4. PATCH /api/enclosures/{id} -> 200 OK
        EnclosureRequestDto patchDto = new EnclosureRequestDto();
        patchDto.setCapacity(6);
        mockMvc.perform(patch("/api/enclosures/" + enclosureId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.capacity", is(6)));

        // 5. DELETE /api/enclosures/{id} -> 204 No Content
        mockMvc.perform(delete("/api/enclosures/" + enclosureId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/enclosures/" + enclosureId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUserCrud() throws Exception {
        // 1. POST /api/users -> 201 Created
        UserRequestDto createDto = new UserRequestDto("ivan_petrov", "Иван Петров", "ivan@example.com", "+79991234567", "ADOPTER");
        MvcResult createResult = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.username", is("ivan_petrov")))
                .andExpect(jsonPath("$.fullName", is("Иван Петров")))
                .andExpect(jsonPath("$.email", is("ivan@example.com")))
                .andReturn();

        JsonNode createdNode = objectMapper.readTree(createResult.getResponse().getContentAsString());
        long userId = createdNode.get("id").asLong();

        // 2. GET /api/users -> 200 OK
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        // 3. GET /api/users/{id} -> 200 OK
        mockMvc.perform(get("/api/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) userId)))
                .andExpect(jsonPath("$.username", is("ivan_petrov")));

        // 4. PATCH /api/users/{id} -> 200 OK
        UserRequestDto patchDto = new UserRequestDto();
        patchDto.setFullName("Иван Иванович Петров");
        mockMvc.perform(patch("/api/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName", is("Иван Иванович Петров")));

        // 5. DELETE /api/users/{id} -> 204 No Content
        mockMvc.perform(delete("/api/users/" + userId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/users/" + userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAdoptionApplicationCrudAndServerTime() throws Exception {
        // Create prerequisite animal and user
        AnimalRequestDto animalDto = new AnimalRequestDto("Шарик", "Собака", 2, "IN_SHELTER", null);
        String animalRes = mockMvc.perform(post("/api/animals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(animalDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long animalId = objectMapper.readTree(animalRes).get("id").asLong();

        UserRequestDto userDto = new UserRequestDto("anna_s", "Анна Смирнова", "anna@example.com", "+79997654321", "ADOPTER");
        String userRes = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long userId = objectMapper.readTree(userRes).get("id").asLong();

        // 1. POST /api/adoption-applications -> 201 Created (createdAt generated on server)
        AdoptionApplicationRequestDto appDto = new AdoptionApplicationRequestDto(animalId, userId, "PENDING");
        MvcResult appResult = mockMvc.perform(post("/api/adoption-applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.animalId", is((int) animalId)))
                .andExpect(jsonPath("$.userId", is((int) userId)))
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andExpect(jsonPath("$.createdAt", notNullValue()))
                .andReturn();

        JsonNode appNode = objectMapper.readTree(appResult.getResponse().getContentAsString());
        long appId = appNode.get("id").asLong();
        String initialCreatedAt = appNode.get("createdAt").asText();
        assertThat(initialCreatedAt).isNotBlank();

        // 2. GET /api/adoption-applications -> 200 OK
        mockMvc.perform(get("/api/adoption-applications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        // 3. GET /api/adoption-applications/{id} -> 200 OK
        mockMvc.perform(get("/api/adoption-applications/" + appId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) appId)))
                .andExpect(jsonPath("$.createdAt", is(initialCreatedAt)));

        // 4. PATCH /api/adoption-applications/{id} -> 200 OK (createdAt preserved)
        AdoptionApplicationRequestDto patchDto = new AdoptionApplicationRequestDto();
        patchDto.setStatus("APPROVED");
        mockMvc.perform(patch("/api/adoption-applications/" + appId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("APPROVED")))
                .andExpect(jsonPath("$.createdAt", is(initialCreatedAt)));

        // 5. DELETE /api/adoption-applications/{id} -> 204 No Content
        mockMvc.perform(delete("/api/adoption-applications/" + appId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/adoption-applications/" + appId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAnimalHandoverRecordCrudAndServerTime() throws Exception {
        // Create prerequisites
        AnimalRequestDto animalDto = new AnimalRequestDto("Мурзик", "Кот", 1, "IN_SHELTER", null);
        String animalRes = mockMvc.perform(post("/api/animals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(animalDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long animalId = objectMapper.readTree(animalRes).get("id").asLong();

        UserRequestDto userDto = new UserRequestDto("olga_k", "Ольга Кузнецова", "olga@example.com", "+79998887766", "ADOPTER");
        String userRes = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long userId = objectMapper.readTree(userRes).get("id").asLong();

        AdoptionApplicationRequestDto appDto = new AdoptionApplicationRequestDto(animalId, userId, "APPROVED");
        String appRes = mockMvc.perform(post("/api/adoption-applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long appId = objectMapper.readTree(appRes).get("id").asLong();

        // 1. POST /api/animal-handover-records -> 201 Created (preparationDate generated, confirmationDate null for DRAFT)
        AnimalHandoverRecordRequestDto recordDto = new AnimalHandoverRecordRequestDto(appId, "DRAFT");
        MvcResult recordResult = mockMvc.perform(post("/api/animal-handover-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recordDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.applicationId", is((int) appId)))
                .andExpect(jsonPath("$.status", is("DRAFT")))
                .andExpect(jsonPath("$.preparationDate", notNullValue()))
                .andExpect(jsonPath("$.confirmationDate", nullValue()))
                .andReturn();

        JsonNode recordNode = objectMapper.readTree(recordResult.getResponse().getContentAsString());
        long recordId = recordNode.get("id").asLong();
        String preparationDate = recordNode.get("preparationDate").asText();
        assertThat(preparationDate).isNotBlank();

        // 2. GET /api/animal-handover-records -> 200 OK
        mockMvc.perform(get("/api/animal-handover-records"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        // 3. GET /api/animal-handover-records/{id} -> 200 OK
        mockMvc.perform(get("/api/animal-handover-records/" + recordId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) recordId)))
                .andExpect(jsonPath("$.preparationDate", is(preparationDate)));

        // 4. PATCH /api/animal-handover-records/{id} -> 200 OK (status -> CONFIRMED, confirmationDate set by server)
        AnimalHandoverRecordRequestDto patchDto = new AnimalHandoverRecordRequestDto();
        patchDto.setStatus("CONFIRMED");
        mockMvc.perform(patch("/api/animal-handover-records/" + recordId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CONFIRMED")))
                .andExpect(jsonPath("$.preparationDate", is(preparationDate)))
                .andExpect(jsonPath("$.confirmationDate", notNullValue()));

        // 5. DELETE /api/animal-handover-records/{id} -> 204 No Content
        mockMvc.perform(delete("/api/animal-handover-records/" + recordId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/animal-handover-records/" + recordId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCascadeDeletionWithoutBrokenReferences() throws Exception {
        // Setup enclosure with animal
        EnclosureRequestDto encDto = new EnclosureRequestDto("Вольер 1", List.of("Кот"), 2);
        String encRes = mockMvc.perform(post("/api/enclosures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(encDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long encId = objectMapper.readTree(encRes).get("id").asLong();

        AnimalRequestDto animalDto = new AnimalRequestDto("Пушок", "Кот", 2, null, encId);
        String animalRes = mockMvc.perform(post("/api/animals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(animalDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long animalId = objectMapper.readTree(animalRes).get("id").asLong();

        // Check animal is inside enclosure
        mockMvc.perform(get("/api/enclosures/" + encId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.animalIds[0]", is((int) animalId)));

        // Create user, application, handover record
        UserRequestDto userDto = new UserRequestDto("user1", "Пользователь 1", "u1@test.com", "123", "ADOPTER");
        String userRes = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long userId = objectMapper.readTree(userRes).get("id").asLong();

        AdoptionApplicationRequestDto appDto = new AdoptionApplicationRequestDto(animalId, userId, "PENDING");
        String appRes = mockMvc.perform(post("/api/adoption-applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long appId = objectMapper.readTree(appRes).get("id").asLong();

        AnimalHandoverRecordRequestDto recordDto = new AnimalHandoverRecordRequestDto(appId, "DRAFT");
        String recRes = mockMvc.perform(post("/api/animal-handover-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recordDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long recId = objectMapper.readTree(recRes).get("id").asLong();

        // 1. Delete enclosure: animal should now have enclosureId = null and placementStatus = IN_SHELTER (no broken pointer)
        mockMvc.perform(delete("/api/enclosures/" + encId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/animals/" + animalId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enclosureId", nullValue()))
                .andExpect(jsonPath("$.placementStatus", is("IN_SHELTER")));

        // 2. Delete animal: should cascade delete application and handover record
        mockMvc.perform(delete("/api/animals/" + animalId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/animals/" + animalId)).andExpect(status().isNotFound());
        mockMvc.perform(get("/api/adoption-applications/" + appId)).andExpect(status().isNotFound());
        mockMvc.perform(get("/api/animal-handover-records/" + recId)).andExpect(status().isNotFound());
    }

    @Test
    void testUserDeletionCascade() throws Exception {
        // Setup animal, user, application, handover record
        AnimalRequestDto animalDto = new AnimalRequestDto("Бобик", "Собака", 3, "IN_SHELTER", null);
        String animalRes = mockMvc.perform(post("/api/animals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(animalDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long animalId = objectMapper.readTree(animalRes).get("id").asLong();

        UserRequestDto userDto = new UserRequestDto("user_to_delete", "Удаляемый Пользователь", "del@test.com", "000", "ADOPTER");
        String userRes = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long userId = objectMapper.readTree(userRes).get("id").asLong();

        AdoptionApplicationRequestDto appDto = new AdoptionApplicationRequestDto(animalId, userId, "PENDING");
        String appRes = mockMvc.perform(post("/api/adoption-applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long appId = objectMapper.readTree(appRes).get("id").asLong();

        AnimalHandoverRecordRequestDto recordDto = new AnimalHandoverRecordRequestDto(appId, "DRAFT");
        String recRes = mockMvc.perform(post("/api/animal-handover-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recordDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long recId = objectMapper.readTree(recRes).get("id").asLong();

        // Delete user: should cascade delete application and handover record
        mockMvc.perform(delete("/api/users/" + userId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/users/" + userId)).andExpect(status().isNotFound());
        mockMvc.perform(get("/api/adoption-applications/" + appId)).andExpect(status().isNotFound());
        mockMvc.perform(get("/api/animal-handover-records/" + recId)).andExpect(status().isNotFound());

        // Animal should still exist
        mockMvc.perform(get("/api/animals/" + animalId)).andExpect(status().isOk());
    }

    @Test
    void testEnclosureRulesValidation() throws Exception {
        // Enclosure for dogs with capacity 1
        EnclosureRequestDto encDto = new EnclosureRequestDto("Вольер для собак", List.of("Собака"), 1);
        String encRes = mockMvc.perform(post("/api/enclosures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(encDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long encId = objectMapper.readTree(encRes).get("id").asLong();

        // Attempt to place a Cat into dog enclosure -> 400 Bad Request
        AnimalRequestDto catDto = new AnimalRequestDto("Васька", "Кот", 2, null, encId);
        mockMvc.perform(post("/api/animals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(catDto)))
                .andExpect(status().isBadRequest());

        // Place Dog 1 into enclosure -> 201 Created
        AnimalRequestDto dog1 = new AnimalRequestDto("Рекс", "Собака", 3, null, encId);
        mockMvc.perform(post("/api/animals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dog1)))
                .andExpect(status().isCreated());

        // Attempt to place Dog 2 when capacity is 1 -> 400 Bad Request
        AnimalRequestDto dog2 = new AnimalRequestDto("Полкан", "Собака", 4, null, encId);
        mockMvc.perform(post("/api/animals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dog2)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testValidationAndNotFoundErrors() throws Exception {
        // Blank animal name -> 400
        AnimalRequestDto invalidAnimal = new AnimalRequestDto("", "Кот", 1, null, null);
        mockMvc.perform(post("/api/animals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidAnimal)))
                .andExpect(status().isBadRequest());

        // Negative age -> 400
        AnimalRequestDto negativeAge = new AnimalRequestDto("Том", "Кот", -1, null, null);
        mockMvc.perform(post("/api/animals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(negativeAge)))
                .andExpect(status().isBadRequest());

        // Non-existent enclosure -> 400
        AnimalRequestDto nonExistentEnc = new AnimalRequestDto("Том", "Кот", 1, null, 99999L);
        mockMvc.perform(post("/api/animals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nonExistentEnc)))
                .andExpect(status().isBadRequest());

        // Application with non-existent foreign keys -> 400
        AdoptionApplicationRequestDto invalidApp = new AdoptionApplicationRequestDto(99999L, 99999L, "PENDING");
        mockMvc.perform(post("/api/adoption-applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidApp)))
                .andExpect(status().isBadRequest());

        // Handover record with non-existent application -> 400
        AnimalHandoverRecordRequestDto invalidRec = new AnimalHandoverRecordRequestDto(99999L, "DRAFT");
        mockMvc.perform(post("/api/animal-handover-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRec)))
                .andExpect(status().isBadRequest());

        // 404 Not Found checks
        mockMvc.perform(get("/api/animals/99999")).andExpect(status().isNotFound());
        mockMvc.perform(get("/api/enclosures/99999")).andExpect(status().isNotFound());
        mockMvc.perform(get("/api/users/99999")).andExpect(status().isNotFound());
        mockMvc.perform(get("/api/adoption-applications/99999")).andExpect(status().isNotFound());
        mockMvc.perform(get("/api/animal-handover-records/99999")).andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/animals/99999")).andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/enclosures/99999")).andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/users/99999")).andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/adoption-applications/99999")).andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/animal-handover-records/99999")).andExpect(status().isNotFound());
    }
}

