# SC2002_Assignment

# Hospital Management System (HMS)

NTU AY2023/24 Semester 1 SC2002 Group Project - Hospital Management System (FYPMS).

Hospital Management System (FYPMS) is a Java console application that utilizes object-oriented concepts to efficiently manage final year project settings. The program is designed with a focus on reusability, extensibility, and maintainability, allowing for easy upgrades and future development. It provides flexibility to accommodate different user types and their requirements.

The initial password for every user is `password`.

## Links

- [Main Page]((https://kuroinit.github.io/SC2002_Assignment/#))
- [GitHub Repository](https://github.com/KuroInit/SC2002_Assignment)
- [Documentation](https://github.com/KuroInit/SC2002_Assignment/tree/main/docs)
- [Report](docs/report)
- [Presentation Video](https://best.pornktube.com/)

- ## Team Members

We are a group 6 from tutorial group A50, Nanyang Technological University, Singapore. There are 4 members in our group:

| Name         | Github Account                                  | Email                 |
|--------------|-------------------------------------------------|-----------------------|
| Venkatesh Arun Moorthy      | [pufanyi](https://github.com/pufanyi)           | [FPU001@e.ntu.edu.sg](mailto:FPU001@e.ntu.edu.sg) |
| Jin Qingyang | [jin-qingyang](https://github.com/jin-qingyang) | [JINQ0003@e.ntu.edu.sg](mailto:JINQ0003@e.ntu.edu.sg) |
| Jiang Jinyi  | [Jinyi087](https://github.com/Jinyi087)         | [D220006@e.ntu.edu.sg](mailto:D220006@e.ntu.edu.sg) |
| Soo Ying Xi  | [niyaojiayou](https://github.com/niyaojiayou)   | [D220001@e.ntu.edu.sg](mailto:D220001@e.ntu.edu.sg)  |

## Project Overview

The Hospital Management System (HMS) is a full-featured healthcare management software developed in Java. It provides a robust platform for managing patients, staff, doctors, appointments, medical records, and patient feedback, with a focus on security, efficiency, and user accessibility. This system is designed using the MVC (Model-View-Controller) architecture to separate concerns, enabling easy maintainability and scalability. The project includes various advanced features like password encryption, feedback collection, and restrictions for weekend appointments.

## Key Features

### 1. User Authentication and Role Management
- **Multi-role Access**: Supports four primary user roles:
  - **Administrator**: Manages hospital staff, medicine inventory, and replenishment requests.
  - **Doctor**: Manages appointments, patient records, and schedules.
  - **Pharmacist**: Manages prescription updates, medication inventory, and restocking requests.
  - **Patient**: Can view and update personal information, schedule and reschedule appointments, and provide feedback.
- **Secure Login**: Each user is authenticated through secure login. Passwords are stored using SHA-256 encryption to prevent unauthorized access.

### 2. Patient Registration
- **Patient Registration**: Patients can register with essential details (e.g., name, date of birth, gender, blood type, email, contact number) and receive a unique ID.
- **Default Password and Password Change**: New patients are assigned a default password, which can be changed upon first login.

### 3. Appointment Management
- **Schedule Management**: Doctors can set and view availability slots for appointments, with patients able to view available slots for scheduling.
- **Booking and Rescheduling**: Patients can schedule, reschedule, or cancel appointments, while doctors can confirm, decline, or view upcoming appointments.
- **Restrictions**: Appointment scheduling on Sundays is restricted, ensuring compliance with non-operational days.

### 4. Medical Records Management
- **Patient Medical Records**: Doctors can add, view, and update patients’ medical records, including diagnosis, treatment plans, and prescribed medications.
- **Appointment Outcomes**: Doctors can record and update the outcome of each appointment, specifying consultation notes, prescribed medications, and medication status.

### 5. Inventory and Replenishment Management
- **Medicine Inventory**: Administrators and pharmacists can view, add, update, or remove medicines from the inventory.
- **Low Stock Alerts**: Provides alerts when medicine stock levels fall below a specified threshold, triggering replenishment requests.
- **Replenishment Requests**: Allows pharmacists to submit and administrators to approve or decline replenishment requests.

### 6. Feedback Collection
- **Patient Feedback**: Patients can submit feedback on their treatment experience, which is stored in a CSV file for later analysis.
- **Rating System**: Patients can rate their experience (1-5 stars) and provide detailed comments, allowing hospital staff to review and improve service quality.

### 7. Password Security
- **Encryption**: Passwords are hashed using SHA-256 encryption via the `Obfuscation` class to ensure sensitive information remains protected.
- **Change Password Option**: After login, users are prompted to change their password for additional security.

## Project Structure

The HMS project is structured as follows:

- **Main**: Entry point for the application, handling the startup sequence, user login, and primary menu display.
- **Models**: Contains data models representing different types of users and records.
- **Views**: Provides CLI-based user interfaces for each role, displaying menus and collecting user input.
- **Controllers**: Manages the application logic for each user role, handling interactions between views and models.
- **Records**: Manages sensitive data like appointment records, feedback, and password hashing.

## Additional Features

### 1. **Password Encryption**
   The system employs SHA-256 hashing for password storage, providing additional security by obfuscating sensitive data.

### 2. **Patient Feedback System**
   Patients can submit feedback along with a rating, which is saved in `patient_feedback.csv`. This feedback enables the hospital to monitor service quality.

### 3. **No Appointments on Sundays**
   The system restricts appointments from being scheduled on Sundays, preventing bookings on days when the hospital is closed.

## Technical Details

### 1. Model-View-Controller (MVC) Architecture
The application uses the MVC architecture to separate concerns. This approach improves modularity and maintainability, ensuring that data, user interface, and control logic remain isolated from one another.

### 2. Data Persistence
All data (e.g., patient details, appointment records, and feedback) is stored in CSV files, simulating a database. Data is read once at startup, and modifications are saved back to the CSV files.

### 3. Error Handling
The application includes error handling for file I/O, user input validation, and exceptional cases such as invalid login attempts.

### 4. Code Structure
The `Main` class serves as the application’s entry point, initiating the program and displaying the main menu. Controllers for each role manage their respective models and views, facilitating data manipulation and display.
