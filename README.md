# project-docker-k8s-java-app

This project demonstrates containerizing a Spring Boot application with a MySQL database using Docker, Docker Compose, and Kubernetes (Minikube).

##  Step 1: Containerize the Application using Docker

### create Dockerfile

'''FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/springboot-mysql-app-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]'''

 ### Build the Docker Image 

docker build -t springboot-mysql-app .

### Tag and Push to Docker Hub

docker tag springboot-mysql-app ranatarek/springboot-mysql-app:latest
docker login
docker push ranatarek/springboot-mysql-app:latest


## Step 2: Create Docker Compose

 ### create docker-compose.yml

'''version: '3.8'

services:
  db:
    image: mysql:8.0
    container_name: mysql-db
    restart: always
    environment:
      MYSQL_DATABASE: mydb
      MYSQL_ROOT_PASSWORD: root
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql

  app:
    build: .
    container_name: springboot-app
    depends_on:
      - db
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://db:3306/mydb
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root

volumes:
  mysql-data: '''

### run command

docker-compose up --build


## Step 3: Kubernetes Deployment (Minikube)
 
### Start Minikube

minikube start

### Create Kubernetes Secret 

create mysql-secret.yaml

'''apiVersion: v1
kind: Secret
metadata:
  name: mysql-secret
type: Opaque
data:
  username: cm9vdA==      
  password: cm9vdA==      

### Create MySQL Deployment

create mysql-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: mysql
spec:
  selector:
    matchLabels:
      app: mysql
  replicas: 1
  template:
    metadata:
      labels:
        app: mysql
    spec:
      containers:
        - name: mysql
          image: mysql:8.0
          ports:
            - containerPort: 3306
          env:
            - name: MYSQL_ROOT_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: mysql-secret
                  key: password
            - name: MYSQL_DATABASE
              valueFrom:
                configMapKeyRef:
                  name: mysql-config
                  key: database

apiVersion: v1
kind: Service
metadata:
  name: mysql
spec:
  selector:
    app: mysql
  ports:
    - protocol: TCP
      port: 3306
      targetPort: 3306 ''' 


### Create App-Deployment

create app-deployment.yaml

'''apiVersion: apps/v1
kind: Deployment
metadata:
  name: springboot-app
spec:
  selector:
    matchLabels:
      app: springboot-app
  replicas: 1
  template:
    metadata:
      labels:
        app: springboot-app
    spec:
      containers:
        - name: springboot-app
          image: ranatarek/springboot-mysql-app:latest   
          ports:
            - containerPort: 8080
          env:
            - name: SPRING_DATASOURCE_URL
              value: jdbc:mysql://mysql:3306/mydb
            - name: SPRING_DATASOURCE_USERNAME
              valueFrom:
                secretKeyRef:
                  name: mysql-secret
                  key: username
            - name: SPRING_DATASOURCE_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: mysql-secret
                  key: password

apiVersion: v1
kind: Service
metadata:
  name: springboot-app
spec:
  selector:
    app: springboot-app
  ports:
    - protocol: TCP
      port: 8080
      targetPort: 8080
  type: ClusterIP '''


 ## Step4 : To Apply K8s Resources run

kubectl apply -f K8s/mysql-secret.yaml
kubectl apply -f K8s/mysql-deployment.yaml
kubectl apply -f K8s/app-deployment.yaml

## Step5 : To Access the Application run

 kubectl port-forward service/springboot-app 8080:8080
 http://localhost:8080/users
 











