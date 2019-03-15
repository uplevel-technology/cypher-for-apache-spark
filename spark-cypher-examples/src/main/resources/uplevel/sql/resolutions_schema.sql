
CREATE TABLE DomainName (
  Value varchar2 not null,

  constraint PK_DomainName primary key (
        Value
    )
);


CREATE TABLE IpAddress(
  Value varchar2 not null,
  Version varchar2 not null,

  constraint PK_IpAddress primary key (
          Value
    )

);

CREATE TABLE InfoSource (
    Id integer not null,
    Name varchar2 not null,

    constraint PK_InfoSource primary key (
      Id
    )

);

CREATE TABLE RESOLUTION
(
  Id integer not null,
  DomainName varchar2 not null,
  ResolvesTo varchar2 not null,
  ExecutedAt TIMESTAMP,
  ExecutedBy integer not null,

  constraint PK_RESOLUTION primary key (
    Id
  ),

  CONSTRAINT FK_RESOLUTION_DOMAIN FOREIGN KEY (
    DomainName
  ) REFERENCES DomainName,

  CONSTRAINT FK_RESOLUTION_RESOLVES_TO FOREIGN KEY (
    ResolvesTo
  ) REFERENCES IPAddress,

    CONSTRAINT FK_RESOLUTION_EXECUTED_BY FOREIGN KEY (
    ExecutedBy
    ) REFERENCES InfoSource,

);
