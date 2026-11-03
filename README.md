## Лабортори №8

Оюутны нэр: Ё.Болормаа
Оюутны код: B222270050


bolopoo@Ubuntu:~/lab08/lab08-mockito$ java -version
openjdk version "17.0.20.1" 2026-08-18
OpenJDK Runtime Environment (build 17.0.20.1+1-1-26.04-Ubuntu)
OpenJDK 64-Bit Server VM (build 17.0.20.1+1-1-26.04-Ubuntu, mixed mode, sharing)


bolopoo@Ubuntu:~/lab08/lab08-mockito$ mvn -version
Apache Maven 3.9.12
Maven home: /usr/share/maven
Java version: 17.0.20.1, vendor: Ubuntu, runtime: /usr/lib/jvm/java-17-openjdk-amd64
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", version: "7.0.0-34-generic", arch: "amd64", family: "unix"


## Тестийн тоо
`results/mvn-test.txt`-ийн сүүлийн мөр: **Tests run: 10, Failures: 0, Errors: 0, Skipped: 0**, BUILD SUCCESS.
Үүнд 7 энгийн тест (A1-A7) болон A8-ийн 3 мөр (параметржүүлсэн тест, мөр бүр тусдаа тоологдоно) орно.

## Хэсэг A: тестүүд

| № | Тест | Хэрэгсэл |
|---|---|---|
| A1 | Идэвхтэй оюутан: "OK"  хадгалалт, и-мэйл  илгээнэ| when + verify |
| A2 | Байхгүй оюутан: "ERROR_NO_STUDENT" | verify(..., never()) |
| A3 | Идэвхгүй оюутан: "ERROR_INACTIVE_STUDENT" | verifyNoInteractions |
| A4 | Өмнөх холбоо хичээл дутуу: "ERROR_PREREQUISITES" | when + never |
| A5 | И-мэйл exception шидсэн ч "OK", хадгалалт хийгдсэн | doThrow |
| A6 | null оролтод IllegalArgumentException (studentId, courseId тус тусад нь) | assertThrows |
| A7 | И-мэйлийн биед хичээлийн код орсон | ArgumentCaptor |
| A8 | Гурван алдааны анги | @ParameterizedTest + @CsvSource |


### A8-ийн lenient() тайлбар
A8-д мөр бүр өөр өөр stub тохируулдаг тул нэг мөрийн stub нь өөр мөрөнд хэрэглэгддэггүй. MockitoExtension-ий strict stubs горим ашиглагдаагүй stub-ыг `UnnecessaryStubbingException`-оор унагаадаг тул `lenient().when(...)` хэлбэрийг ашигласан.


## Хэсэг B: мини мутаци

| Мутант | Эвдрэл | Унасан тестүүд | Үр дүн (файл) |
|---|---|---|---|
| 1 | Идэвхгүй оюутны шалгалт (`!student.active()`) комментлосон | A3: `register_idevhgui_oyutan_ERROR_INACTIVE_STUDENT_email_ogt_haritsaagui`; A8 `[2]` (INACTIVE_STUDENT мөр) | Failures: 2, BUILD FAILURE (`results/mvn-test-mutant-1.txt`) |
| 2 | `saveRegistration(...)` дуудалтыг комментлосон | A1: `register_idevhtei_oyutan_OK_butsaaj_hadgalaad_email_ilgeene`; A5: `register_email_exception_shidsen_ch_burtgel_OK_khadgalalt_khiigdsen` | Failures: 2, BUILD FAILURE (`results/mvn-test-mutant-2.txt`) |
| 3 | Эхний if-ээс `\|\| courseId == null` устгасан | A6: `register_null_orolt_IllegalArgumentException_shidne_hamaarluudtai_haritsaagui` | Failures: 1, BUILD FAILURE (`results/mvn-test-mutant-3.txt`) |


**Дүгнэлт:**
 Гурван мутант бүгд баригдсан, цоорхой олдсонгүй. Мутант 1-д кодыг эвдэхэд `ERROR_INACTIVE_STUDENT`-ын оронд `ERROR_NO_COURSE` буцсан тул A3 ба A8 унав. Мутант 2-ыг `verify(repository).saveRegistration(...)` барьсан (Wanted but not invoked). Мутант 3-ыг A6 эхнээс нь барьсан, учир нь A6 нь `studentId` ба `courseId`-ийн null-ийг тус тусад нь шалгадаг. Тиймээс тестийг нэмж засах шаардлага гараагүй. Мутаци бүрийн дараа `git checkout src/main`-аар эх кодыг сэргээж, эцэст нь `results/mvn-test.txt`-ийг дахин ногоон болгож баталгаажуулсан.




