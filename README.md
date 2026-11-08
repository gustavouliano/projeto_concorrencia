# Concurrent Excel File Processing via Socket

A client-server application in Java. The client sends sales spreadsheets from several branches to the server, which processes each one **in parallel** and builds a consolidated Excel report. The report goes back to the client and is also sent by email.

> Academic project for Concurrent Programming · [Article on Medium](https://medium.com/@jenifergoedert10/processamento-concorrencial-de-arquivos-excel-e-análise-de-dados-de-filiais-via-socket-8b6dda42b71d) (in Portuguese)

## How it works

1. The **client** sends the spreadsheets (`Filial1.xlsx` … `Filial5.xlsx`) over port **7001**.
2. The **server** creates one task per file in an `ExecutorService`. Each task calculates:
   - total sales;
   - the best-selling product;
   - the region with the highest sales value.
3. The results go into `RelatorioFiliais.xlsx`, which is emailed and sent back to the client.

| Class | Role |
|-------|------|
| `client/Cliente.java` | Sends the spreadsheets and saves the report it receives |
| `server/Servidor.java` | Accepts connections (one thread per client) and coordinates the processing |
| `server/Leitura.java` | `Runnable` that reads one spreadsheet and calculates its metrics |
| `server/Relatorio.java` | Builds the consolidated spreadsheet |
| `server/Email.java` | Sends the report through Gmail SMTP |

**Input spreadsheet format:** Cliente (customer) | Região (region) | Produto (product) | Quantidade (quantity) | Valor (unit price). The first row is the header.

## Stack

- **Java 8+**: TCP sockets, `DataInputStream`/`DataOutputStream`, threads and the Executor Framework
- **Apache POI**: reading and writing `.xlsx` files
- **JavaMail**: emailing the report

## How to run

**1. Dependencies.** Put these JARs in a `lib/` folder: [Apache POI](https://poi.apache.org/download.html) (with its dependencies), `javax.mail` 1.6.2 and `javax.activation`. `Email.java` also imports `jakarta.activation.DataHandler` but never uses it: delete that line or add the `jakarta.activation-api` JAR.

**2. Change the hardcoded paths in the code:**
- `Cliente.java`: set `caminho` to the project's `src/files/` folder.
- `Email.java`: set the path to `dados.properties` and the recipient address (`"e-mail que recebe"`).

**3. Credentials.** Create `src/properties/dados.properties` (this folder is ignored by Git). Use a [Gmail app password](https://myaccount.google.com/apppasswords):
```properties
prop.email=your.email@gmail.com
prop.email.password=your_app_password
```
If you don't want to send the email, comment out the `Email.enviaEmail(...)` call in `Servidor.java`.

**4. Compile and run** (start the server before the client):
```bash
javac -cp "lib/*" -d bin src/server/*.java src/client/*.java
java -cp "bin:lib/*" server.Servidor   # terminal 1
java -cp "bin:lib/*" client.Cliente    # terminal 2
```
On Windows, use `;` instead of `:` in the classpath. In Eclipse, add the JARs to the *Build Path*, then run `Servidor` and then `Cliente`.

The report is saved to `src/files/RelatorioFiliais.xlsx`.
