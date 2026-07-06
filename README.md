# Concurrent Excel Processing and Branch Data Analysis via Socket

A client-server application developed in Java to process multiple Excel files concurrently and generate consolidated business reports. The solution uses socket communication, multithreading and the Executor Framework to improve processing performance and automate data analysis.

> **Academic Project** — Concurrent Programming.

Medium: https://medium.com/@jenifergoedert10/processamento-concorrencial-de-arquivos-excel-e-análise-de-dados-de-filiais-via-socket-8b6dda42b71d

---

## 🚀 Features

- Client-server architecture using sockets
- Concurrent processing of multiple Excel files
- Multithreaded execution with the Executor Framework
- Automated branch sales analysis
- Consolidated Excel report generation
- Automatic report delivery via email
- Efficient processing of large datasets

---

## 🛠 Technologies

- Java
- Socket Programming
- Executor Framework
- Apache POI
- JavaMail API
- Multithreading

---

## 🏗 Architecture

The application consists of two main components:

- **Client:** Sends multiple Excel files to the server through socket communication.
- **Server:** Receives the files, processes each one concurrently using the Executor Framework, consolidates the results and generates a final Excel report, which is returned to the client and sent via email.

---

## 📊 Analysis Performed

The application generates a consolidated report containing:

- Total sales by branch
- Best-selling products
- Quantity sold per product
- Region with the highest sales volume
- Total sales value by region

---

## 🎯 Learning Outcomes

- Concurrent Programming
- Multithreading
- Socket Programming
- Client-Server Architecture
- Excel File Processing
- Data Analysis
- Report Automation


