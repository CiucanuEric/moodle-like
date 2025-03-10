
# Moodle - Simplified Educational Platform

This project is a simplified version of the Moodle educational platform, designed to provide core learning management functionalities. Developed using Kotlin, Java, and React, it utilizes Docker, Spring Boot, and Envoy Proxy for a scalable and efficient architecture. The main communication protocol is HTTP, with gRPC used as a secondary protocol for authentication.

## Features

-   **User Authentication**: Secure login system using gRPC.
    
-   **Course Management**: Create, edit, and manage educational courses.
    
-   **Assignment Submissions**: Users can submit and track assignments.
    
-   **Scalability**: Docker and Envoy Proxy for containerized deployment.
    
-   **Database Storage**: Efficient data storage with MySQL and MongoDB.
    

## Technologies Used

-   **Kotlin & Java**: Core backend programming languages.
    
-   **Spring Boot**: Backend framework for handling application logic.
    
-   **React**: Frontend framework for a dynamic user interface.
    
-   **Docker**: Containerization for easy deployment.
    
-   **Envoy Proxy**: Filters HTTP/2 packets from the gRPC module to the React service.
    
-   **gRPC**: Secure and efficient authentication mechanism.
    
-   **MySQL & MongoDB**: Used for structured and unstructured data storage.
    

## Installation & Setup

1.  Clone the repository:
    
    ```
    git clone https://github.com/CiucanuEric/moodle-like.git
    ```
    
2.  Navigate to the project folder:
    
    ```
    cd moodle-like
    ```
    
3.  Set up Docker and start the containers:
    
    ```
    docker-compose up --build
    ```
    
4.  Open your browser and go to `http://localhost:8080/`.
    

## Customization

-   Modify `application.properties` for database settings.
    
-   Update React components in the `frontend/` directory.
    
-   Enhance `AuthService` for custom authentication rules.
    

## Contributing

If you'd like to contribute, feel free to fork the repository and submit a pull request with your improvements.

## Contact

For any inquiries, reach out to me via:

-   Email: magda0701@gmail.com
    
-   LinkedIn: [Eric Ciucanu](https://www.linkedin.com/in/eric-ciucanu-457003286/)
    

----------

Thank you for using the Moodle-like Educational Platform! 🎓
