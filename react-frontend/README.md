# 🧾 Budžeta Pārvaldības Sistēma

Java Spring Boot + React aplikācija, kas ļauj lietotājam:
- 📤 Augšupielādēt PDF un attēlu čekus (ar OCR apstrādi)
- ✍️ Manuāli ievadīt čekus un produktus
- 📊 Analizēt tēriņus pēc kategorijām un datuma diapazoniem
- 📂 Pārvaldīt produktu kategorijas
- 🔍 Apskatīt kopējos tēriņus, statistiku un produktu sadalījumu

---

## ⚙️ Tehnoloģijas
- **Back-end:** Java 17, Spring Boot, JPA, MySQL
- **Front-end:** React, Tailwind CSS, Axios, React Router
- **OCR:** Tesseract (latviešu valodas atbalsts)
- **Datubāze:** MySQL (vai H2 lokālai testēšanai)
- **PDF:** Apache PDFBox

---

## 🚀 Palaišana

### 🔙 Back-end
```bash
cd backend
./mvnw spring-boot:run
```

### 🔜 Front-end
```bash
cd frontend
npm install
npm run dev
```

### 🛢️ Datubāzes konfigurācija (application.properties)
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/budzets
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
spring.jpa.hibernate.ddl-auto=update
```

---

## 📂 Projekta struktūra
```
budzets/
├── backend/       # Spring Boot aplikācija
├── frontend/      # React lietotāja saskarne
├── scripts/       # OCR skripti, PDF/attēlu testfaili
├── tessdata/      # Tesseract .traineddata valodu faili
└── README.md
```

---

## 🧠 Galvenās funkcijas
- 📁 Automātiska datu nolasīšana no PDF un .JPG čekiem (tostarp atlaides)
- 📅 Filtrēšana pēc datuma, mēneša, gada
- 💸 Izdevumu statistika pēc kategorijām
- 🧾 Manuāla čeku veidošana un esošo produktu izvēle
- 🗑️ Čeku dzēšana un labošana

---

## 🖼️ Ekrānuzņēmumi
![example](./screenshots/receipt-example.jpg)

---

## 🧪 Testēšana
- Vienkārši REST testēšanas scenāriji (Postman / cURL)
- JUnit testu piemēri nākotnē (atbalstīti)

---

## 📈 Iespējamie uzlabojumi nākotnē
- 🔐 Autentifikācija un autorizācija
- 📱 Mobilajām ierīcēm pielāgota versija (PWA)
- 📤 Eksports uz Excel / PDF
- 🔎 Labāka OCR precizitāte (EasyOCR, Google Vision API)
- 🧠 Produkta kategorijas automātiska atpazīšana

---

## 👨‍💻 Autors
**Mārcis A.**