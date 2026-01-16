# Maven-aggregation Quickstart

Maven-aggregation Quickstart is an IntelliJ IDEA plugin designed to quickly generate standard multi-module Maven project structures. With this plugin, you can create modular projects with one click and automatically configure dependencies between modules. It also supports custom module names and Java versions, making it perfect for rapid Java project startup.

---

## 📌 Key Features

- **One-click creation of Maven multi-module project structure**
- **Automatic generation of dependencies between modules**
- **Support for custom module names**
- **Support for Java version selection (8/11/17/21)**
- **Automatic generation of project README.md and .gitignore files**
- **Support for multi-module architecture, single-module DDD architecture, and single-module MVC architecture**
- **Automatic addition of common dependencies (Lombok, Hutool, SLF4J, Logback, Swagger, JUnit, etc.)**

---

## 🚀 Usage

### 1. Install the Plugin

1. Open IntelliJ IDEA.
2. Go to `Settings (Preferences)` > `Plugins`.
3. Click on the `Marketplace` tab.
4. Search for `Maven-aggregation Quickstart`.
5. Click `Install` to install the plugin.
6. Restart IntelliJ IDEA.

### 2. Create a Project

1. Create an empty project in IntelliJ IDEA.
2. Click on the menu `File > New > Create Maven Aggregation Project`.
3. Fill in the following information:
    - GroupId: The organization identifier for the Maven project (e.g. `com.example`)
    - ArtifactId: Project name
    - Version: Project version (default `1.0.0`)
    - Select architecture mode: Multi-module architecture / Single-module DDD architecture / Single-module MVC architecture
    - Module names: Enter multiple module names (e.g. `api`, `service`, `pojo`)
    - Java version: Select the Java version used by the project (8/11/17/21)
    - Common dependencies: Select dependencies to add (Lombok, Hutool, SLF4J, etc.)
4. Click `Finish`, and the plugin will automatically generate the project structure.

---

## 📁 Project Structure Examples

### Multi-module Architecture Project Structure
```
your-project-name/
├── api/              # API interface module exposed to the outside world
├── service/          # Business logic module
├── mapper/           # Data access layer module
├── pojo/             # Entity class module
├── common/           # Public utility class and common component module
└── pom.xml           # Parent Maven configuration file

Each module contains the following standard directory structure:
module-name/
├── src/
│   ├── main/
│   │   ├── java/           # Java source code
│   │   └── resources/      # Static resource files
│   └── test/
│       └── java/           # Test code
└── pom.xml                 # Module-level Maven configuration file
```

### Single-module DDD Architecture Project Structure
```
your-project-name/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/yourproject/
│   │   │       ├── application/     # Application layer
│   │   │       ├── domain/          # Domain layer
│   │   │       ├── infrastructure/  # Infrastructure layer
│   │   │       └── interfaces/      # Interface layer
│   │   └── resources/
│   └── test/
│       └── java/
├── pom.xml
├── README.md
└── .gitignore
```

### Single-module MVC Architecture Project Structure
```
your-project-name/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/yourproject/
│   │   │       ├── controller/      # Controller layer
│   │   │       ├── service/         # Service layer
│   │   │       │   └── impl/        # Service implementation
│   │   │       ├── mapper/          # Mapper/DAO layer
│   │   │       ├── entity/          # Entity/Model layer
│   │   │       ├── model/           # DTO/VO, etc.
│   │   │       └── common/          # Common utilities
│   │   └── resources/
│   └── test/
│       └── java/
├── pom.xml
├── README.md
└── .gitignore
```

---

## 🧩 Module Descriptions

### Multi-module Architecture Module Descriptions

| Module Name | Description |
|-------------|-------------|
| `api`       | API interface module exposed to the outside world |
| `common`    | Public utility class and common component module |
| `mapper`    | Data access layer module (usually used for database operations) |
| `pojo`      | Entity class module |
| `service`   | Business logic module |

> Note: You can customize module names, and the plugin will automatically recognize and generate corresponding descriptions.

### Single-module DDD Architecture Layer Descriptions

| Layer | Description |
|-------|-------------|
| `application`     | Application layer, responsible for business process control and use case implementation |
| `domain`          | Domain layer, containing core business logic and domain models |
| `infrastructure`  | Infrastructure layer, providing technical implementation and external dependencies |
| `interfaces`      | Interface layer, responsible for providing external service interfaces |

### Single-module MVC Architecture Layer Descriptions

| Layer | Description |
|-------|-------------|
| `controller`     | Controller layer, handles external requests |
| `service`        | Service layer, business logic |
| `mapper`         | Data access layer (Mapper/DAO) |
| `entity`         | Entity/Model layer |
| `common`         | Common utilities and shared components |

---

## 🧪 Building the Project

The generated Maven project can be built using standard Maven commands:

```bash
# Clean the project
mvn clean

# Compile the project
mvn compile

# Run tests
mvn test

# Package the project
mvn package

# Install to local repository
mvn install
```

---

## 🛠️ Plugin Development and Building

If you want to modify or build the plugin from source code, please follow these steps:

### 1. Clone the Repository
```
git clone https://github.com/yourname/maven-aggregation.git
cd maven-aggregation
```

### 2. Build the Plugin

Use Gradle to build the plugin:

```bash
# Clean the project
./gradlew clean

# Build the project
./gradlew build
```

After building, the plugin `.jar` file will be generated in the `build/libs/` directory.

### 3. Install the Plugin in IntelliJ IDEA

1. Open IntelliJ IDEA.
2. Go to `Settings (Preferences)` > `Plugins`.
3. Click `Install Plugin from Disk...`.
4. Select `build/libs/maven-aggregation-1.1.1.jar`.
5. Install and restart IntelliJ IDEA.

---

## 💖 Support the Project

If you find this plugin helpful, please consider supporting the project. Your support will help us continue to improve and maintain this plugin. You can support us through the following ways:

<table>
  <tr align="center">
    <td>
      <strong>Alipay</strong><br>
      <em>(Chinese users)</em>
    </td>
    <td>
      <strong>WeChat Pay</strong><br>
      <em>(Chinese users)</em>
    </td>
    <td>
      <strong>PayPal</strong><br>
      <em>(International users)</em>
    </td>
  </tr>
  <tr align="center">
    <td><img src="donate/alipay_qr.png" width="150" alt="Alipay QR Code"></td>
    <td><img src="donate/wechat_qr.png" width="150" alt="WeChat Pay QR Code"></td>
    <td>
      <a href="https://www.paypal.me/yourname">
        <img src="https://img.shields.io/badge/PayPal-003087?style=for-the-badge&logo=paypal&logoColor=white" alt="PayPal">
      </a>
    </td>
  </tr>
  <tr align="center">
    <td>
      <strong>Buy Me a Coffee</strong><br>
      <em>(International users)</em>
    </td>
    <td>
      <strong>GitHub Sponsors</strong><br>
      <em>(International users)</em>
    </td>
    <td>
      <strong>Open Collective</strong><br>
      <em>(International users)</em>
    </td>
  </tr>
  <tr align="center">
    <td>
      <a href="https://www.buymeacoffee.com/yourname">
        <img src="https://img.shields.io/badge/Buy%20Me%20a%20Coffee-ffdd00?style=for-the-badge&logo=buy-me-a-coffee&logoColor=black" alt="Buy Me a Coffee">
      </a>
    </td>
    <td>
      <a href="https://github.com/tingyu9315">
        <img src="https://img.shields.io/badge/GitHub%20Sponsors-EA4AAA?style=for-the-badge&logo=github&logoColor=white" alt="GitHub Sponsors">
      </a>
    </td>
    <td>
      <a href="https://opencollective.com/yourname">
        <img src="https://img.shields.io/badge/Open%20Collective-7FADF2?style=for-the-badge&logo=open-collective&logoColor=white" alt="Open Collective">
      </a>
    </td>
  </tr>
</table>

---

## 📞 Contact Us

If you have any questions or suggestions, please contact:

📧 Email: 1938023944@qq.com  
🌐 Website: [https://www.wandong.com](https://www.wandong.com)
