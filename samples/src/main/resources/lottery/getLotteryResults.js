window.location.href = "http://www.millipiyango.gov.tr/sonuclar/_cs_superloto.php";

var sonuclar = {};
var sonuclarArray = [];
document.querySelectorAll("#superloto-tarihList option").forEach(function(cekilisTarih) {
    var url = 'cekilisler/superloto/' + cekilisTarih.value + '.json';
    jQuery.getJSON(url, function(json) {
        var rakamlar = json.data.rakamlar.split('#');
        for(i=0; i<rakamlar.length; i++) {
            rakamlar[i] = Number.parseInt(rakamlar[i]);
        }
        rakamlar.sort(function (a, b) { return a - b; });
        sonuclar[cekilisTarih.value] = rakamlar;
        sonuclarArray.push(rakamlar);
    });
});

console.log(JSON.stringify(sonuclar));