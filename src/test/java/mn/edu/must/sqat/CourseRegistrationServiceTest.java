package mn.edu.must.sqat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CourseRegistrationServiceTest {
    @Mock StudentRepository repository;
    @Mock EmailSender emailSender;
    
    CourseRegistrationService service;
    
     @BeforeEach
    void setUp() {
        service = new CourseRegistrationService(repository, emailSender);
    }
    
     private Student activeStudent() {
        return new Student("S1", "s1@must.edu.mn", true, List.of("CS101"));
    }
    
    // A1
    
    @Test
    void register_idevhtei_oyutan_OK_butsaaj_hadgalaad_email_ilgeene() {
        when(repository.findStudent("S1")).thenReturn(activeStudent());
        when(repository.courseExists("CS201")).thenReturn(true);
        when(repository.prerequisites("CS201")).thenReturn(List.of("CS101"));

        String result = service.register("S1", "CS201");

        assertEquals("OK", result);
        verify(repository).saveRegistration("S1", "CS201");
        verify(emailSender).send(eq("s1@must.edu.mn"), anyString(), contains("CS201"));
    }
    
    // A2
    
    @Test
    void register_baihgui_oyutan_ERROR_NO_STUDENT_hadgalahgui_email_yavahgui() {
        when(repository.findStudent("S9")).thenReturn(null);

        String result = service.register("S9", "CS201");

        assertEquals("ERROR_NO_STUDENT", result);
        verify(repository, never()).saveRegistration(anyString(), anyString());
        verify(emailSender, never()).send(anyString(), anyString(), anyString());
    }
    
    // A3
    
    @Test
    void register_idevhgui_oyutan_ERROR_INACTIVE_STUDENT_email_ogt_haritsaagui() {
        when(repository.findStudent("S2"))
                .thenReturn(new Student("S2", "s2@must.edu.mn", false, List.of()));

        String result = service.register("S2", "CS201");

        assertEquals("ERROR_INACTIVE_STUDENT", result);
        verifyNoInteractions(emailSender);
        verify(repository, never()).saveRegistration(anyString(), anyString());
    }
    
    // A4
    
    @Test
    void register_umnuh_holbootoi_hicheel_dutuu_ERROR_PREREQUISITES_hadgalaagui() {
        when(repository.findStudent("S1")).thenReturn(activeStudent());
        when(repository.courseExists("CS301")).thenReturn(true);
        when(repository.prerequisites("CS301")).thenReturn(List.of("CS101", "CS201"));

        String result = service.register("S1", "CS301");

        assertEquals("ERROR_PREREQUISITES", result);
        verify(repository, never()).saveRegistration(anyString(), anyString());
    }
    
    // A5 
    
    @Test
    void register_email_exception_shidsen_ch_burtgel_OK_khadgalalt_khiigdsen() {
        when(repository.findStudent("S1")).thenReturn(activeStudent());
        when(repository.courseExists("CS201")).thenReturn(true);
        when(repository.prerequisites("CS201")).thenReturn(List.of());
        doThrow(new RuntimeException("SMTP down"))
                .when(emailSender).send(anyString(), anyString(), anyString());

        String result = service.register("S1", "CS201");

        assertEquals("OK", result);
        verify(repository).saveRegistration("S1", "CS201");
    }
    
    // A6 — хоёр аргумент тус бүрийн null-ийг шалгана (мутант 3-ыг барина)
    @Test
    void register_null_orolt_IllegalArgumentException_shidne_hamaarluudtai_haritsaagui() {
        assertThrows(IllegalArgumentException.class, () -> service.register(null, "CS201"));
        assertThrows(IllegalArgumentException.class, () -> service.register("S1", null));
        assertThrows(IllegalArgumentException.class, () -> service.register(null, null));

        verifyNoInteractions(repository, emailSender);
    }
    
    // A7
    @Test
    void register_amjilttai_burtgel_email_biyd_khicheeliin_kod_orson() {
        when(repository.findStudent("S1")).thenReturn(activeStudent());
        when(repository.courseExists("CS201")).thenReturn(true);
        when(repository.prerequisites("CS201")).thenReturn(List.of());

        service.register("S1", "CS201");

        ArgumentCaptor<String> body = ArgumentCaptor.forClass(String.class);
        verify(emailSender).send(eq("s1@must.edu.mn"), anyString(), body.capture());
        assertTrue(body.getValue().contains("CS201"),
                "email bied hicheeliin kod baih ystoi, harin: " + body.getValue());
    }
    
    
     // A8 — 3 мөр = 3 тест. Мөр бүр өөр stub хэрэглэдэг тул lenient().
    @ParameterizedTest(name = "{0} -> {1}")
    @CsvSource({
            "NO_STUDENT,       ERROR_NO_STUDENT",
            "INACTIVE_STUDENT, ERROR_INACTIVE_STUDENT",
            "PREREQUISITES,    ERROR_PREREQUISITES"
    })
    void register_aldaanii_angi_bur_taarsan_kod_butsaana_hadgalahgui(String scenario, String expected) {
        switch (scenario) {
            case "NO_STUDENT" ->
                    lenient().when(repository.findStudent("S1")).thenReturn(null);
            case "INACTIVE_STUDENT" ->
                    lenient().when(repository.findStudent("S1"))
                            .thenReturn(new Student("S1", "s1@must.edu.mn", false, List.of()));
            case "PREREQUISITES" -> {
                lenient().when(repository.findStudent("S1")).thenReturn(activeStudent());
                lenient().when(repository.courseExists("CS301")).thenReturn(true);
                lenient().when(repository.prerequisites("CS301")).thenReturn(List.of("CS999"));
            }
            default -> throw new IllegalStateException(scenario);
        }

        assertEquals(expected, service.register("S1", "CS301"));
        verify(repository, never()).saveRegistration(anyString(), anyString());
        verify(emailSender, never()).send(anyString(), anyString(), anyString());
    }
}
    
    

    
    
   
   

    



