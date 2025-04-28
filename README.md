# 👾 Budget Management System

A Java Spring Boot + React application that allows users to:
- 📤 Upload PDF receipts from Maxima
- ✍️ Manually enter receipts and products
- 📊 Analyze expenses by categories and date ranges
- 📂 Manage product categories
- 🔍 View total expenses, statistics, and product breakdowns

---

## ⚙️ Technologies
- **Back-end:** Java 17, Spring Boot, JPA, MySQL
- **Front-end:** React, Tailwind CSS, Axios, React Router
- **Database:** MySQL
- **PDF Processing:** Apache PDFBox

---

## 🚀 Getting Started

### 🖙 Back-end
```bash
cd backend
./mvnw spring-boot:run
```

### 🖜 Front-end
```bash
cd frontend
npm install
npm run dev
```

### 📂 Database Configuration (application.properties)
```properties
spring.application.name=budzets

spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://${MYSQL_HOST:localhost}:3306/budzets?useUnicode=yes&characterEncoding=UTF-8
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

---

## 📂 Project Structure
```
budzets/
├── backend/       # Spring Boot application
├── frontend/      # React user interface
├── scripts/       # Scripts for faster Maxima receipt import
├── tessdata/      # Tesseract .traineddata language files
└── README.md
```

---

## 🧐 Key Features
- 📁 Automatic data extraction from Maxima PDF receipts
- 🗕️ Filtering by date, month, and year
- 💸 Expense statistics by categories
- 📾 Manual receipt creation and product selection
- 🗑️ Receipt deletion and editing

---

## 🖼️ Screenshots
![example](./screenshots/receipt-example.jpg)

---

## 📈 Future Improvements
- 🔐 Authentication and authorization
- 📱 Mobile-friendly version (PWA)
- 🔎 Automatic parsing of receipts from other stores
- 🧐 Automatic product category recognition
- Improved front-end design
- Expanded functionality

---

## 👨‍💻 Author
**Mārcis A.**

## 🐳 Running with Docker

This project can be easily run using Docker and Docker Compose.

### 💡 Requirements
- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)

### ▶️ Run
```bash
docker-compose up --build
```

This will build and start:
- The back-end (Spring Boot application)
- The front-end (React user interface)
- A MySQL database container

Back-end will be available at: [http://localhost:8080](http://localhost:8080)  
Front-end will be available at: [http://localhost:3000](http://localhost:3000)

### 📂 Docker File Structure
```
budzets/
├── backend/
│   ├── Dockerfile           # Spring Boot Docker configuration
│   └── ...
├── frontend/
│   ├── Dockerfile           # React Docker configuration
│   └── ...
├── docker-compose.yml       # Service definitions
├── .dockerignore            # Files ignored by Docker
└── README.md