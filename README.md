# SimpleJDBC

[![](https://jitpack.io/v/SMontiel/SimpleJDBC.svg)](https://jitpack.io/#SMontiel/SimpleJDBC) [![Build Status](https://travis-ci.org/SMontiel/SimpleJDBC.svg?branch=master)](https://travis-ci.org/SMontiel/SimpleJDBC)

Java library for simple SQL querying (through JDBC) :slightly_smiling_face:

## Download

You can download a jar from GitHub's [releases page](https://github.com/SMontiel/SimpleJDBC/releases).

Or use Gradle:

#### **Step 1.** Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

```groovy
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```

#### **Step 2.** Add the dependency

```groovy
dependencies {
  compile 'com.github.SMontiel:SimpleJDBC:0.0.3'
}
```

## How do I use `SimpleJDBC`?

### Initialization

Get a `DataSource` from any database library connector, let me show you an example with `MariaDB`:

``` java
MariaDbDataSource dataSource = null;
try {
  dataSource = new MariaDbDataSource("localhost", 3306, "db_name");
  dataSource.setUser("username");
  dataSource.setPassword("yourPowerfulPassword");
} catch (SQLException e) {
  e.printStackTrace();
}
```

Now, create your `SimpleJDBC` object passing the `DataSource` to it:

```java
SimpleJDBC db = SimpleJDBC.from(dataSource);
```

That's it! 

Lets suppose we have a schema like:

```sql
CREATE TABLE person (
  id INT,
  name VARCHAR(20),
  last_name VARCHAR(20)
);
INSERT INTO person(id, name, last_name) VALUES(1, "Jack", "Ryan");
INSERT INTO person(id, name, last_name) VALUES(2, "Sherlock", "Holmes");
INSERT INTO person(id, name, last_name) VALUES(3, "Edmond", "Dantès");
```

If we want to see all records, we have to do this:

```java
// Java 7
List<String> list = 
  db.any("SELECT * FROM person", new ThrowingFunction<ResultSet, String>() {
      @Override
      public String apply(ResultSet rs) throws Exception {
        return rs.getString("id")
            + " :: " + rs.getString("name")
            + " :: " + rs.getString("last_name");
      }
  });
for (String person : list) System.out.println(person);

// Java 8
List<String> list = db.any("SELECT * FROM person", 
                rs -> rs.getString("id") 
                        + " :: " + rs.getString("name") 
                        + " :: " + rs.getString("last_name"));
list.forEach(System.out::println);
```

We'll see in console something like:

```bash
1 :: Jack :: Ryan
2 :: Sherlock :: Holmes
3 :: Edmond :: Dantès
```

So, if we want to get only one record of a person:

```java
String QUERY = "SELECT * FROM person WHERE id = 3";
// Java 7
Person person = db.one(QUERY, new ThrowingFunction<ResultSet, Person>() {
      @Override
      public Person apply(ResultSet rs) throws Exception {
        String name = rs.getString("name");
        return new Person(name, rs.getString("last_name"));
      }
  });

System.out.println(person.getName() + " " + person.getLastName()
                  + " is The Count of Monte Cristo.");

// Java 8
Person person = db.one(QUERY, rs -> {
        String name = rs.getString("name");
        return new Person(name, rs.getString("last_name"));
    });

System.out.println(person.getName() + " " + person.getLastName()
        + " is The Count of Monte Cristo.");
```

We expect in console:

```bash
Edmond Dantès is The Count of Monte Cristo.
```

## Build

Building `SimpleJDBC` with Gradle is fairly straight forward:

```bash
git clone https://github.com/SMontiel/SimpleJDBC.git
cd SimpleJDBC
./gradlew jar
```

## Contributing

Do you want to contribute or give me a hand a hand with the documentation? I will appreciate that!
