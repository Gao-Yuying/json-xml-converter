# json-xml-converter
A program that parses JSON to XML and vice versa.

## Examples
### JSON to XML
Input (file `testJson1.txt`):
```json
{
    "transactions": {
        "id": "6753322",
        "data": [
            123,
            true,
            false,
            [ ],
            [],
            { },
            {},
            [
                1, 2, 3,
                {
                    "@attr": "value6",
                    "#element": "value7"
                }
            ],
            null,
            "",
            {
                "key1": "value1",
                "key2": {
                    "@attr": "value2",
                    "#key2": "value3"
                }
            },
            {
                "@attr2": "value4",
                "#element": "value5"
            }
        ]
    }
}
```
Output:
```
Converting result of testJson1.txt:

(In document form)

<transactions>
  <id>6753322</id>
  <data>
    <element>123</element>
    <element>true</element>
    <element>false</element>
    <element></element>
    <element></element>
    <element></element>
    <element></element>
    <element>
      <element>1</element>
      <element>2</element>
      <element>3</element>
      <element attr="value6">value7</element>
    </element>
    <element />
    <element></element>
    <element>
      <key1>value1</key1>
      <key2 attr="value2">value3</key2>
    </element>
    <element attr2="value4">value5</element>
  </data>
</transactions>


(In element hierarchy form)

Element:
path = transactions

Element:
path = transactions, id
value = "6753322"

Element:
path = transactions, data

Element:
path = transactions, data, element
value = "123"

Element:
path = transactions, data, element
value = "true"

Element:
path = transactions, data, element
value = "false"

Element:
path = transactions, data, element
value = ""

Element:
path = transactions, data, element
value = ""

Element:
path = transactions, data, element
value = ""

Element:
path = transactions, data, element
value = ""

Element:
path = transactions, data, element

Element:
path = transactions, data, element, element
value = "1"

Element:
path = transactions, data, element, element
value = "2"

Element:
path = transactions, data, element, element
value = "3"

Element:
path = transactions, data, element, element
value = "value7"
attributes:
attr = "value6"

Element:
path = transactions, data, element
value = null

Element:
path = transactions, data, element
value = ""

Element:
path = transactions, data, element

Element:
path = transactions, data, element, key1
value = "value1"

Element:
path = transactions, data, element, key2
value = "value3"
attributes:
attr = "value2"

Element:
path = transactions, data, element
value = "value5"
attributes:
attr2 = "value4"
```
### XML to JSON
Notice that in the output JSON, `@` means tag attribute and `#` means tag content.

Input (file `testXml1.txt`):
```xml
<?xml version = "1.0" encoding = "utf-8"?>
<transactions>
    <transaction>
        <id>6753322</id>
        <number region = "Russia">8-900-000-00-00</number>
        <date day = "12" month = "12" year = "2018"/>
        <amount currency="EUR">1000.00</amount>
        <completed>true</completed>
    </transaction>
    <transaction>
        <id>67533244</id>
        <number region = "Russia">8-900-000-00-01</number>
        <date day = "13" month = "12" year = "2018"/>
        <amount currency ="RUB">2000.00</amount>
        <completed>true</completed>
    </transaction>
    <transaction>
        <id>67533257</id>
        <number region="Russia">8-900-000-00-02</number>
        <date day = "14" month = "12" year = "2018"/>
        <amount currency = "EUR">3000.00</amount>
        <completed>false</completed>
    </transaction>
    <transaction>
        <id>67533259</id>
        <number region = "Ukraine">8-900-000-00-03</number>
        <date day = "15" month = "12" year = "2018"/>
        <amount currency = "GRN">4000.00</amount>
        <completed>false</completed>
    </transaction>
    <transaction>
        <id>67533566</id>
        <number region = "Ukraine">8-900-000-00-04</number>
        <date day = "16" month = "12" year = "2018"/>
        <amount currency = "USD">5000.00</amount>
        <completed>false</completed>
    </transaction>
</transactions>
```
Output:
```
Converting result of testXml1.txt:

(In document form)

{
  "transactions" : [
      {
          "id" : "6753322",
          "number" : {
            "@region" : "Russia",
            "#number" : "8-900-000-00-00"
          },
          "date" : {
            "@day" : "12",
            "@month" : "12",
            "@year" : "2018",
            "#date" : null
          },
          "amount" : {
            "@currency" : "EUR",
            "#amount" : "1000.00"
          },
          "completed" : "true"
      },
      {
          "id" : "67533244",
          "number" : {
            "@region" : "Russia",
            "#number" : "8-900-000-00-01"
          },
          "date" : {
            "@day" : "13",
            "@month" : "12",
            "@year" : "2018",
            "#date" : null
          },
          "amount" : {
            "@currency" : "RUB",
            "#amount" : "2000.00"
          },
          "completed" : "true"
      },
      {
          "id" : "67533257",
          "number" : {
            "@region" : "Russia",
            "#number" : "8-900-000-00-02"
          },
          "date" : {
            "@day" : "14",
            "@month" : "12",
            "@year" : "2018",
            "#date" : null
          },
          "amount" : {
            "@currency" : "EUR",
            "#amount" : "3000.00"
          },
          "completed" : "false"
      },
      {
          "id" : "67533259",
          "number" : {
            "@region" : "Ukraine",
            "#number" : "8-900-000-00-03"
          },
          "date" : {
            "@day" : "15",
            "@month" : "12",
            "@year" : "2018",
            "#date" : null
          },
          "amount" : {
            "@currency" : "GRN",
            "#amount" : "4000.00"
          },
          "completed" : "false"
      },
      {
          "id" : "67533566",
          "number" : {
            "@region" : "Ukraine",
            "#number" : "8-900-000-00-04"
          },
          "date" : {
            "@day" : "16",
            "@month" : "12",
            "@year" : "2018",
            "#date" : null
          },
          "amount" : {
            "@currency" : "USD",
            "#amount" : "5000.00"
          },
          "completed" : "false"
      }
  ]
}

(In element hierarchy form)

Element:
path = transactions

Element:
path = transactions, transaction

Element:
path = transactions, transaction, id
value = "6753322"

Element:
path = transactions, transaction, number
value = "8-900-000-00-00"
attributes:
region = "Russia"

Element:
path = transactions, transaction, date
value = null
attributes:
day = "12"
month = "12"
year = "2018"

Element:
path = transactions, transaction, amount
value = "1000.00"
attributes:
currency = "EUR"

Element:
path = transactions, transaction, completed
value = "true"

Element:
path = transactions, transaction

Element:
path = transactions, transaction, id
value = "67533244"

Element:
path = transactions, transaction, number
value = "8-900-000-00-01"
attributes:
region = "Russia"

Element:
path = transactions, transaction, date
value = null
attributes:
day = "13"
month = "12"
year = "2018"

Element:
path = transactions, transaction, amount
value = "2000.00"
attributes:
currency = "RUB"

Element:
path = transactions, transaction, completed
value = "true"

Element:
path = transactions, transaction

Element:
path = transactions, transaction, id
value = "67533257"

Element:
path = transactions, transaction, number
value = "8-900-000-00-02"
attributes:
region = "Russia"

Element:
path = transactions, transaction, date
value = null
attributes:
day = "14"
month = "12"
year = "2018"

Element:
path = transactions, transaction, amount
value = "3000.00"
attributes:
currency = "EUR"

Element:
path = transactions, transaction, completed
value = "false"

Element:
path = transactions, transaction

Element:
path = transactions, transaction, id
value = "67533259"

Element:
path = transactions, transaction, number
value = "8-900-000-00-03"
attributes:
region = "Ukraine"

Element:
path = transactions, transaction, date
value = null
attributes:
day = "15"
month = "12"
year = "2018"

Element:
path = transactions, transaction, amount
value = "4000.00"
attributes:
currency = "GRN"

Element:
path = transactions, transaction, completed
value = "false"

Element:
path = transactions, transaction

Element:
path = transactions, transaction, id
value = "67533566"

Element:
path = transactions, transaction, number
value = "8-900-000-00-04"
attributes:
region = "Ukraine"

Element:
path = transactions, transaction, date
value = null
attributes:
day = "16"
month = "12"
year = "2018"

Element:
path = transactions, transaction, amount
value = "5000.00"
attributes:
currency = "USD"

Element:
path = transactions, transaction, completed
value = "false"
```
