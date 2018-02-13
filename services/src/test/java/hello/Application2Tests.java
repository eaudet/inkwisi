/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package hello;

import com.jarics.inkwisi.Application;
import com.jarics.inkwisi.entities.Person;
import com.jarics.inkwisi.repositories.PersonRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class Application2Tests {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonRepository personRepository;

    @Before
    public void deleteAllBeforeTests() throws Exception {
        personRepository.deleteAll();
    }

    @Test
    public void shouldReturnRepositoryIndex() throws Exception {

        mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$._links.people").exists());
    }

    @Test
    public void shouldCreateEntity() throws Exception {

        mockMvc.perform(post("/people").content("{\"firstName\": \"Frodo\", \"lastName\":\"Baggins\"}")).andExpect(status().isCreated()).andExpect(header().string("Location", containsString("people/")));
    }

    @Test
    public void shouldRetrieveEntity() throws Exception {

        MvcResult mvcResult = mockMvc.perform(post("/people").content("{\"firstName\": \"Frodo\", \"lastName\":\"Baggins\"}")).andExpect(status().isCreated()).andReturn();

        String location = mvcResult.getResponse().getHeader("Location");
        mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(jsonPath("$.firstName").value("Frodo")).andExpect(jsonPath("$.lastName").value("Baggins"));
    }

    @Test
    public void testController() throws Exception {

        Person wPerson = new Person();
        wPerson.setFirstName("Erick");
        wPerson.setLastName("Audet");

        mockMvc.perform(post("/personV2/create").contentType(contentType).content(TestUtil.convertObjectToJsonBytes(wPerson))).andExpect(status().isOk());

        mockMvc.perform(get("/personV2/define?name={name}", "Audet")).andExpect(status().isOk()).andDo(MockMvcResultHandlers.print()).andExpect(jsonPath("$[0].lastName").value("Audet"));
    }

    @Test
    public void testVoiceDefinition() {
        try {

            Path path = Paths.get("/Users/erickaudet/dev/inkwisiApp/voices/SamuelLJackson/I have had Enough.mp3");
            byte[] data = Files.readAllBytes(path);

            MockMultipartFile file =
                    new MockMultipartFile("file", "I have had Enough.mp3", null, data);

            mockMvc.perform(fileUpload("/personV2/define/voice")
                    .file(file)).andExpect(status().isOk());
        } catch (Exception e) {

        }

    }

    @Test
    public void testLearnVoice() {
        try {

            Path path = Paths.get("/Users/erickaudet/dev/inkwisiApp/voices/SamuelLJackson/I have had Enough.mp3");
            byte[] data = Files.readAllBytes(path);

            MockMultipartFile file =
                    new MockMultipartFile("file", "I have had Enough.mp3", null, data);

            mockMvc.perform(fileUpload("/personV2/learn/voice?name={name}")
                    .file(file)).andExpect(status().isOk());
        } catch (Exception e) {

        }

    }

    @Test
    public void shouldQueryEntity() throws Exception {

        mockMvc.perform(post("/people").content("{ \"firstName\": \"Frodo\", \"lastName\":\"Baggins\"}")).andExpect(status().isCreated());

        mockMvc.perform(get("/people/search/findByLastName?name={name}", "Baggins")).andExpect(status().isOk()).andExpect(jsonPath("$._embedded.people[0].firstName").value("Frodo"));
    }

    @Test
    public void shouldUpdateEntity() throws Exception {

        MvcResult mvcResult = mockMvc.perform(post("/people").content("{\"firstName\": \"Frodo\", \"lastName\":\"Baggins\"}")).andExpect(status().isCreated()).andReturn();

        String location = mvcResult.getResponse().getHeader("Location");

        mockMvc.perform(put(location).content("{\"firstName\": \"Bilbo\", \"lastName\":\"Baggins\"}")).andExpect(status().isNoContent());

        mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(jsonPath("$.firstName").value("Bilbo")).andExpect(jsonPath("$.lastName").value("Baggins"));
    }

    @Test
    public void shouldPartiallyUpdateEntity() throws Exception {

        MvcResult mvcResult = mockMvc.perform(post("/people").content("{\"firstName\": \"Frodo\", \"lastName\":\"Baggins\"}")).andExpect(status().isCreated()).andReturn();

        String location = mvcResult.getResponse().getHeader("Location");

        mockMvc.perform(patch(location).content("{\"firstName\": \"Bilbo Jr.\"}")).andExpect(status().isNoContent());

        mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(jsonPath("$.firstName").value("Bilbo Jr.")).andExpect(jsonPath("$.lastName").value("Baggins"));
    }

    @Test
    public void shouldDeleteEntity() throws Exception {

        MvcResult mvcResult = mockMvc.perform(post("/people").content("{ \"firstName\": \"Bilbo\", \"lastName\":\"Baggins\"}")).andExpect(status().isCreated()).andReturn();

        String location = mvcResult.getResponse().getHeader("Location");
        mockMvc.perform(delete(location)).andExpect(status().isNoContent());

        mockMvc.perform(get(location)).andExpect(status().isNotFound());
    }

}