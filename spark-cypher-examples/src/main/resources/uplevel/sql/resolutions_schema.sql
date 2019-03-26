
CREATE TABLE DNS_RESOLUTION (
   DomainName VARCHAR2 NOT NULL,
   IPAddress VARCHAR2 NOT NULL,
   Timestamp TIMESTAMP NOT NULL,
   Id INTEGER NOT NULL,

   constraint PkDns primary key (
        Id
     )
)

