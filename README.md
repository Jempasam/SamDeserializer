# SamDeserializer
## FR
Une super bibliothèque pour charger et sauvegarder des données depuis des fichiers dans des formats variés.
### SimiliJSON
```javascript
shop: {
	shelf: {
		vendeur: "Jean Marc",
		contenu: "steak" "merguez" "chicken"
	}
}
```
Plusieurs modificateurs à utiliser sur les données chargées permettent d'ajouter des fonctionnalités à la syntaxe. Comme des fonctionnalités de génération aléatoire.
### Génération aléatoire
```javascript
<viande>: "steak" "langue de boeuf" "bavette" "beefsteal" "poulet" "cuisse de poulet" "dinde" "cote de porc" "jambon" "saucisson" "jambon sec" "veau" "chipolata" "merguez" "saucisse" "brochette",
<fromage>: "camembert" "brie" "caprice des dieux" "chevre" "chevre frai" "mozarella" "burrata" "feta" "parmesan" "conte" "mimolette" "gouda" "fromage à raclette" "fromage rape" "kiri" "babibelle" "fromage blanc" "fromage frai",
<noms>: "Jean Marc" "Paul Alexandre" "Fred Glodux" "Anne boustifaille",
<rayon>: {
	vendeur: "<name:noms>",
	contenu: "<name:viande;repeat:3;group:stuff>",
	contenu: "<name:fromage;repeat:3;group:stuff>"
},
magasin: {
	rayon: "<name:rayon;repeat:1:3>"
}
```
Résultat:
```javascript
"magasin": {
  "rayon": {
	 "vendeur": "Jean Marc",
	 "contenu": "merguez",
	 "contenu": "dinde",
	 "contenu": "saucisse"
  },
  "rayon": {
	 "vendeur": "Anne boustifaille",
	 "contenu": "fromage blanc",
	 "contenu": "feta",
	 "contenu": "kiri"
  }
```
Un hydrateur pour créer des objets de classe annotées à partir des données chargées.
```java
@Loadable
public class ColorPBucket implements PaintBucket {
	@LoadableParameter
	private Color color;

	public ColorPBucket(Color color) {
		super();
		this.color = color;
	}
	
	@Override
	public Color getColor(int x, int y, float px, float py) {
		return color;
	}
	
	//LOADABLE
	private ColorPBucket() { }
}
```
```javascript
"plain_red":{
	type: ColorPBucket,
	color: #ff0000
}
```