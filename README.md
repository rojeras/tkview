# TkView
TkView är tänkt att erbjuda ett modernare och mer förändringsbart gränssnitt mot information om tjänstedomäner och tjänstekontrakt. 

## Externa beroenden
* DOMDB-API: http://api.ntjp.se/dominfo/doc/index.html. För information om tjänstedomäner och -kontrakt.
* TAK-API: http://api.ntjp.se/coop/doc/index.html. Information om vilka tjänstekontrakt som är installerade i en tjänsteplattform.
* TPDB-API: ID-begrepp i [hippo](https://integrationer.tjansteplattform.se)

## Integration i webbplatsen [rivta.se](https://rivta.se)
Dagens (2020-10-27) rivta.se har en ganska rörig struktur, troligen baserad på att sidorna till en del är baserade på Bootstrap-mallar, till en del består av genererad, statisk, html. TkView ska ersätta två menyval; "Tjänstedomäner" och "Tjänstekontrakt". Båda dessa har samma struktur. En HTML-fil (domains.html respektive interaction_index.html) definierar ett stort antal CSS-er och huvud- och fot meny. Därefter öppnas en iframe med respektive "domains/index.html" respektive "domains/interaction_index.html". 

TkView är en applikation - inte HTML-sidor. Den hanterar webbläsarhistoriken genom att själv lägga på ändelser på URL-en. Detta fungerar inte speciellt bra när den publiceras via en iframe. Det försvårar också möjligheten att direktlänka till domän- och kontraktslistorna, samt till specifika domänsidor. Onödiga omladdningar måste också undvikas eftersom det tar relativt mycket tid att hämta "datadumpen" via DOMDB-api. 
I stället har en startupsida skapats, tkwiki.html som skall ligga i root-mappen på rivta.se. Den är i princip en kopia på domains.html, men i stället för att öppna en iframe till statiska htmlsidor så invokeras TkView-applikationen.

På detta sätt kan TkWiki invokeras genom någon av följande länkar:
* https://rivta.se/tkwiki.html#/domains - för att gå direkt till listan av domäner
* https://rivta.se/tkwiki.html#/contracts - för att gå direkt till listan av kontrakt
* https://rivta.se/tkwiki.html#/domain/[domännamn] - för att gå direkt till en sida som beskriver [domännamn] 

## Installation
Gör följande steg för att installera TkWiki:
1. Skapa mappen "tkview" direkt under public_html på rivta.se
1. Packa upp tkview.zip i tkview-mappen.
1. Uppdatera topp-menyn på alla sidor på rivta.se. 
    * Länkten till Tjänstedomäner skall ändras till:
    
        ` <li class="active"><a href="tkview.html#/domains">Tjänstedomäner</a></li>`
    * Länken till Tjänstekontrakt skall ändras till:
    
        `<li><a href="tkview.html#/contracts">Tjänstekontrakt</a></li>`

    * Exempel på dessa länkar återfinns i tkview.html. 
    
## Uppsättning av utvecklingsmiljö
Det är lite intrikat att sätta upp utvecklingsmiljön eftersom det finns ganska stora beroenden på bl a CSS-er på rivta.se. Här beskrivs hur en lokal rivta-se miljö skapas och används. 

1. Ladda ner en kopia av rivta.se:

    `wget -r http://rivta.se`
1. Gå till root-mappen:

    `cd rivta.se`
1. Skapa en symbolisk länk från distribution-mappen i IntelliJs utvecklingsmiljö. I min setup blir det:

    `ln -s ../../dev/rivta/build/distributions tkview`

1. Starta en webbserver i root-mappen. Ex 

    ```
    python3 -m http.server 
   ```
1. Test i browsern via länken http://localhost:8000/tkview/#/domains

## Test direkt standalone 
http://localhost:4000/standalone.html#/domains

## KDoc-dokumentation   
KDoc är Kotlins motsvarighet till Javadoc. TkView är dokumenterat mha KDoc. Använd följande steg för att generera dokumentationen:

1. Klona detta repo
2. Kör gradle-tasken `tkview/Tasks/documentation/dokkaHtml`
3. Öppna filen `build/dokka/html/tkview/index.html`