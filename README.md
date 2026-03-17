# Java Web Framework with Docker and AWS EC2 Deployment (AREP Workshop)

This project implements a lightweight web application framework in Java (without Spring), using reflection and annotations for route registration, with concurrent request handling and graceful shutdown. It includes Docker containerization, Docker Hub publishing flow, and AWS EC2 deployment steps.

Repository: https://github.com/Rogerrdz/Modularizaci-n_con_virtualizaci-n_e_Introducci-n_a_Docker_Arquitecturas_Empresariales.git

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing. The Maven module is inside AppWebServer_Docker, so all build and run commands must be executed from that folder.

### Prerequisites

What things you need to install the software and how to install them:

- Java 17+
- Maven 3.8+
- Docker Desktop (or Docker Engine)
- Git

```bash
java -version
mvn -version
docker --version
git --version
```

### Installing

A step by step series of examples that tell you how to get a development environment running.

Say what the step will be: clone the repository.

```bash
git clone https://github.com/Rogerrdz/Modularizaci-n_con_virtualizaci-n_e_Introducci-n_a_Docker_Arquitecturas_Empresariales.git
```

And repeat: enter the repository and then the Maven module.

```bash
cd Modularizaci-n_con_virtualizaci-n_e_Introducci-n_a_Docker_Arquitecturas_Empresariales
cd AppWebServer_Docker
```

And repeat: compile and install dependencies/artifacts.

```bash
mvn clean install
mvn compile
```

End with an example of getting some data out of the system or using it for a little demo.

```powershell
java -cp "target/classes;target/dependency/*" edu.escuelaing.arep.MicroSpringBoot2
```

Then open:

- http://localhost:8082/index.html
- http://localhost:8082/hello
- http://localhost:8082/hello?name=Roger

## Running the tests

Explain how to run the automated tests for this system:

```bash
cd AppWebServer_Docker
mvn test
```

### Break down into end to end tests

These tests validate framework behavior and endpoint responses (routes, query params, and route registry behavior).

```bash
mvn -Dtest=AppTest test
```

## Deployment

Add additional notes about how to deploy this on a live system.

Local Docker deployment:

```bash
cd AppWebServer_Docker
mvn clean package
docker build -t Rogerrdz/webframework-arep:latest .
docker run -d -p 42000:6000 --name web-aws Rogerrdz/webframework-arep:latest
docker ps
```

Docker Compose deployment:

```bash
cd AppWebServer_Docker
docker compose up -d
docker ps
```

Docker Hub publishing:

```bash
docker login
docker push Rogerrdz/webframework-arep:latest
```

AWS EC2 deployment notes:

```bash
sudo yum update -y
sudo yum install docker -y
sudo service docker start
sudo usermod -a -G docker ec2-user
```

After relogin:

```bash
docker login
docker run -d -p 42000:6000 --name web-aws Rogerrdz/webframework-arep:latest
docker ps
```

Remember to open inbound rule Custom TCP 42000 in the instance Security Group.

## Evidence

### Running the tests

Test execution results (`mvn test`):

![Test Execution](AppWebServer_Docker/src/images/test_execution.png)

### Local Execution Evidence

Server running locally with compiled classes:

![Local Execution](AppWebServer_Docker/src/images/execution_MicroSpringBoot2..png)

### AWS EC2 Evidence

EC2 instance created:

![EC2 Instance](AppWebServer_Docker/src/images/created_instance.png)

Local folder with SSH keys and compiled classes:

![SSH Folder](AppWebServer_Docker/src/images/folder_with_keys_and_classes.png)

SFTP connection to upload files to the server:

![SFTP Connection](AppWebServer_Docker/src/images/sftp_connection.png)

Installing Amazon Corretto 21 on the server:

![Corretto 21 Install](AppWebServer_Docker/src/images/Install_the_JDK_for_Amazon_Corretto_21_server.png)

![Corretto 21 Install Complete](AppWebServer_Docker/src/images/Install_the_JDK_for_Amazon_Corretto_21_server_complete%20.png)

Unzipping compiled classes on the server:

![Unzip Classes](AppWebServer_Docker/src/images/unzip_calsses.zip.png)

Security Group inbound rules configuration:

![Security Group Rules](AppWebServer_Docker/src/images/unboundes_rules_security_group_server.png)

### Application accessed via public DNS

Server running and responding via EC2 public DNS:

![Public DNS](AppWebServer_Docker/src/images/correct_execution_with_public_DNS_server.png)

Home page (`/index.html`):

![Index](AppWebServer_Docker/src/images/correct_execution_with_public_DNS_server_index.png)

Route `/pi`:

![PI](AppWebServer_Docker/src/images/correct_execution_with_public_DNS_server_pi.png)

Route `/euler`:

![Euler](AppWebServer_Docker/src/images/correct_execution_with_public_DNS_server_e.png)

Route `/hello`:

![Hello](AppWebServer_Docker/src/images/correct_execution_with_public_DNS_server_hello.png)

Route `/hello?name=Roger`:

![Hello with name](AppWebServer_Docker/src/images/correct_execution_with_public_DNS_server_hello_name.png)

![Hello with name 2](AppWebServer_Docker/src/images/correct_execution_with_public_DNS_server_hello_name2.png)

Route `/greeting?name=Juan`:

![Greeting](AppWebServer_Docker/src/images/correct_execution_with_public_DNS_server_greeting_name.png)

Route `/greeting/bye?name=Juan`:

![Greeting Bye](AppWebServer_Docker/src/images/correct_execution_with_public_DNS_server_greeting_bye.png)

Route `/greeting/welcome?name=Roger`:

![Greeting Welcome](AppWebServer_Docker/src/images/correct_execution_with_public_DNS_server_greeting_welcome.png)

## Built With

* [Java 17](https://www.oracle.com/java/) - Programming language
* [Maven](https://maven.apache.org/) - Dependency Management
* [Docker](https://www.docker.com/) - Containerization
* [AWS EC2](https://aws.amazon.com/ec2/) - Cloud virtual machine deployment

## Contributing

Please open an issue to discuss major changes and submit your pull request with a clear description of the update.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/Rogerrdz/Modularizaci-n_con_virtualizaci-n_e_Introducci-n_a_Docker_Arquitecturas_Empresariales/tags).

## Authors

* **Roger Rodriguez** - *Initial work* - [Rogerrdz](https://github.com/Rogerrdz)

## Acknowledgments

* AREP course workshop guidelines
* Escuela Colombiana de Ingenieria
* Inspiration from annotation-driven web frameworks