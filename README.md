# SC2002_Assignment

Introduction

The Hospital Management System (HMS) is a comprehensive application designed to streamline and automate hospital operations. This system is implemented with an MVC (Model-View-Controller) architecture to separate concerns, ensure data security, and enhance maintainability. It supports patient, doctor, pharmacist, and administrator roles with unique functionalities for each, such as appointment scheduling, patient records, and medical inventory management.

Features

Core Features
User Registration and Authentication:
New users can register, and existing users can log in with role-based access.
Role-Based Access Control:
Distinct menus and functionalities for doctors, patients, pharmacists, and administrators.
Patient Feedback Collection:
Patients can submit feedback and ratings, stored securely for hospital review.
Password Encryption:
SHA-256 hashing is used to securely store user passwords.
Additional Features
No Appointments on Sundays:
The system automatically prevents scheduling appointments on Sundays.
Memoized Data Loading:
Data is loaded once at startup to reduce file I/O during runtime.
Secure User Management:
Includes password encryption and role-based access permissions.
Project Structure

The project is organized into folders based on the MVC architecture:

healthcare.users: Contains the user classes with specific models, views, and controllers for each role.
healthcare.records: Stores records-related classes, including medical records, appointments, and feedback.
healthcare.main: The entry point (Main.java) that handles system startup, login, and menu display.
Key Classes
Model: DoctorModel, PatientModel, PharmacistModel, AdministratorModel
View: DoctorView, PatientView, PharmacistView, AdministratorView
Controller: DoctorController, PatientController, PharmacistController, AdministratorController
Main Class: Main.java
