import * as THREE from 'three';
import { OrbitControls } from 'three/addons/controls/OrbitControls.js';
import { CSS2DRenderer, CSS2DObject } from 'three/addons/renderers/CSS2DRenderer.js';
import { geoMercator, geoConicConformal, geoContains, geoCentroid } from '../vendor/d3-geo.esm.js';
import polylabel from '../vendor/polylabel.esm.js';

export async function cargarJson(ruta) {
  const respuesta = await fetch(ruta, { cache: 'no-store' });
  if (!respuesta.ok) throw new Error('HTTP ' + respuesta.status + ' en ' + ruta);
  return respuesta.json();
}

export function poligonosDe(feature) {
  const c = feature.geometry.coordinates;
  return feature.geometry.type === 'Polygon' ? [c] : c;
}

function superficieAnillo(anillo) {
  let suma = 0;
  for (let i = 0; i < anillo.length; i++) {
    const [x1, z1] = anillo[i];
    const [x2, z2] = anillo[(i + 1) % anillo.length];
    suma += x1 * z2 - x2 * z1;
  }
  return suma;
}

function reorientarAnillos(anillos) {
  const copia = anillos.map(a => a.slice());
  if (copia.length === 0) return copia;
  if (superficieAnillo(copia[0]) < 0) copia[0] = copia[0].slice().reverse();
  for (let k = 1; k < copia.length; k++) {
    if (superficieAnillo(copia[k]) > 0) copia[k] = copia[k].slice().reverse();
  }
  return copia;
}

export function crearMapaGeo(config) {
  const {
    anchoMapa = 120,
    altoMapa = 150,
    alturaMapa = 0.9,
    alturaNodo = 4.2,
    duracionTramo = 3.1,
    proyeccion: crearProyeccion,
    invertirFn,
    centroPaisFn,
    dentroDelPaisFn,
    corregirDentroFn,
    crearEtiquetaExtra,
    construirRutaFn,
    prepararPanel,
    rutaCargo,
    rutaParaguay,
    rutaNexus,
    totalEntidades,
    textoLeyenda,
    titulo,
    obtenerPoblacionTexto,
    calcularRegalos,
    obtenerRegaloTexto,
    obtenerEntregaTexto,
    mostrarSectorPersonalizado,
    construirPuntosRuta,
    usarRutasMaritimas = false,
    crearTrineoFn
  } = config;

  const consola = document.getElementById('consola');
  const badge = document.getElementById('badge');
  const badgeTexto = document.getElementById('badgeTexto');
  const elDept = document.getElementById('deptActual');
  const elCapital = document.getElementById('capital');
  const elPoblacion = document.getElementById('poblacion');
  const elEstado = document.getElementById('estadoEntrega');
  const elEntrega = document.getElementById('entrega');
  const capaCarga = document.getElementById('carga');
  const leyenda = document.getElementById('leyenda');
  const tituloEl = document.getElementById('titulo-radar');
  if (tituloEl && titulo) tituloEl.textContent = titulo;
  if (leyenda && textoLeyenda) {
    leyenda.innerHTML = textoLeyenda.map(t => '<div>' + t + '</div>').join('');
  }

  if (prepararPanel) prepararPanel({ elDept, elCapital, elPoblacion, elEstado, elEntrega });

  let entregas = 0;
  let regalosTotales = 0;
  const entregados = new Set();

  function hora() { return new Date().toTimeString().slice(0, 8); }
  function registrar(texto, tipo) {
    const el = document.createElement('div');
    el.className = 'evento ' + (tipo || '');
    el.innerHTML = '<span class="hora">' + hora() + '</span>' + texto;
    consola.prepend(el);
    while (consola.childElementCount > 100) consola.removeChild(consola.lastChild);
  }

  function actualizarEntrega() {
    if (obtenerEntregaTexto) elEntrega.textContent = obtenerEntregaTexto({ entregas, regalosTotales, totalEntidades });
    else elEntrega.textContent = 'REGALOS: ' + regalosTotales.toLocaleString('es-ES') + ' · ENTREGAS: ' + entregas + ' / ' + totalEntidades;
  }

  function mostrarSector(d, estado) {
    if (mostrarSectorPersonalizado) { mostrarSectorPersonalizado(d, estado, { elDept, elCapital, elPoblacion, elEstado }); return; }
    elDept.textContent = (d.romano ? d.romano + ' · ' : '') + d.nombre;
    elCapital.textContent = d.capital || '-';
    elPoblacion.textContent = d.poblacionTexto || '-';
    elEstado.textContent = estado;
  }

  let escena, camara, controles, renderizador, renderizadorEtiquetas;
  let geojson, pais, proyeccion, centroPais;
  let mapaDept = [], nodosDept = [], seleccionables = [], rutaOrden = [];
  let curvaRuta, trineo, auraTrineo, luzOro, rejilla, grupoMapa;
  let indiceSectorActivo = -1, ultimoControlSector = -1;

  function proyectar(lon, lat) {
    if (proyeccion) {
      const p = proyeccion([lon, lat]);
      return { x: p[0] - anchoMapa / 2, z: p[1] - altoMapa / 2 };
    }
    return { x: 0, z: 0 };
  }

  function invertir(x, z) {
    if (invertirFn) return invertirFn(x, z, { anchoMapa, altoMapa });
    if (proyeccion && proyeccion.invert) {
      const p = proyeccion.invert([x + anchoMapa / 2, z + altoMapa / 2]);
      return { lon: p[0], lat: p[1] };
    }
    return { lon: 0, lat: 0 };
  }

  function crearForma(anillo) {
    const forma = new THREE.Shape();
    anillo.forEach((punto, i) => {
      const q = proyectar(punto[0], punto[1]);
      if (i === 0) forma.moveTo(q.x, -q.z); else forma.lineTo(q.x, -q.z);
    });
    forma.closePath();
    return forma;
  }

  function crearFormas(feature) {
    return poligonosDe(feature).map((poligono) => {
      const forma = crearForma(poligono[0]);
      for (let k = 1; k < poligono.length; k++) {
        const agujero = new THREE.Path();
        poligono[k].forEach((punto, i) => {
          const q = proyectar(punto[0], punto[1]);
          if (i === 0) agujero.moveTo(q.x, -q.z); else agujero.lineTo(q.x, -q.z);
        });
        agujero.closePath();
        forma.holes.push(agujero);
      }
      return forma;
    });
  }

  function coordenadasDeAnillo(anillo) {
    return anillo.map((punto) => {
      const q = proyectar(punto[0], punto[1]);
      return [q.x, q.z];
    });
  }

  function poloDeEtiqueta(feature) {
    let mejor = null, mejorArea = -1;
    for (const poligono of poligonosDe(feature)) {
      const anillo = coordenadasDeAnillo(poligono[0]);
      const area = Math.abs(THREE.ShapeUtils.area(anillo.map((p) => new THREE.Vector2(p[0], p[1]))));
      if (area > mejorArea) { mejorArea = area; mejor = poligono; }
    }
    if (!mejor) return { x: 0, z: 0 };
    const anillos = mejor.map((anillo) => coordenadasDeAnillo(anillo));
    const rer = reorientarAnillos(anillos);
    const punto = polylabel(rer, 0.2);
    return { x: punto[0], z: punto[1] };
  }

  function dentroDelPais(pos) {
    if (dentroDelPaisFn) return dentroDelPaisFn(pos, { invertir, pais });
    const g = invertir(pos.x, pos.z);
    return geoContains(pais, [g.lon, g.lat]);
  }

  function corregirDentro(pos) {
    if (corregirDentroFn) return corregirDentroFn(pos, { dentroDelPais, centroPais });
    if (dentroDelPais(pos)) return pos.clone();
    const destino = centroPais.clone();
    for (let f = 0.9; f >= 0.1; f -= 0.1) {
      const prueba = pos.clone().lerp(destino, f);
      if (dentroDelPais(prueba)) return prueba;
    }
    return destino.clone();
  }

  function crearEtiquetaSector(feature, indice) {
    if (crearEtiquetaExtra) { crearEtiquetaExtra(feature, indice, { poloDeEtiqueta, proyecto: proyectar, escena, grupoMapa, alturaMapa, css2d: CSS2DObject }); return; }
    const punto = poloDeEtiqueta(feature);
    const pequeña = false;
    const div = document.createElement('div');
    div.className = 'etiqueta-depto' + (pequeña ? ' pequena' : '');
    div.textContent = feature.properties.nombre || feature.properties.nombreOficial;
    const etiqueta = new CSS2DObject(div);
    etiqueta.position.set(punto.x, alturaMapa + 0.2, punto.z);
    escena.add(etiqueta);
  }

  function construirMapa() {
    const paleta = config.paleta || [0x0f8f7a, 0x12a08a, 0x1bb39a, 0x0d7d6c, 0x159c84];
    const coloresComunidad = config.coloresComunidad || {};
    geojson.features.forEach((feature, i) => {
      const formas = crearFormas(feature);
      const geometria = new THREE.ExtrudeGeometry(formas, { depth: alturaMapa, bevelEnabled: false, curveSegments: 1 });
      geometria.rotateX(-Math.PI / 2);
      const com = feature.properties.comunidad || feature.properties.region;
      const col = coloresComunidad[com] || paleta[i % paleta.length];
      const material = new THREE.MeshStandardMaterial({
        color: col, emissive: col, emissiveIntensity: 0.16, transparent: true, opacity: 0.46,
        side: THREE.DoubleSide, roughness: 0.55, metalness: 0.25, depthWrite: false
      });
      const malla = new THREE.Mesh(geometria, material);
      const bordes = new THREE.LineSegments(new THREE.EdgesGeometry(geometria, 24),
        new THREE.LineBasicMaterial({ color: 0x54ffe0, transparent: true, opacity: 0.55 }));
      const grupo = new THREE.Group();
      grupo.add(malla);
      grupo.add(bordes);
      grupoMapa.add(grupo);

      const cap = proyectar(feature.properties.capitalLon, feature.properties.capitalLat);
      const nodo = new THREE.Group();
      nodo.position.set(cap.x, alturaMapa, cap.z);
      const anillo = new THREE.Mesh(new THREE.TorusGeometry(1.9, 0.12, 8, 34),
        new THREE.MeshBasicMaterial({ color: 0x00ffcc, transparent: true, opacity: 0.8 }));
      anillo.rotation.x = -Math.PI / 2;
      nodo.add(anillo);
      const pilar = new THREE.Mesh(new THREE.CylinderGeometry(0.22, 0.22, alturaNodo, 10),
        new THREE.MeshBasicMaterial({ color: 0x00ffcc, transparent: true, opacity: 0.35 }));
      pilar.position.y = alturaNodo / 2;
      nodo.add(pilar);
      const esfera = new THREE.Mesh(new THREE.SphereGeometry(0.58, 16, 16),
        new THREE.MeshStandardMaterial({ color: 0x00ffcc, emissive: 0x00ffcc, emissiveIntensity: 1.4, roughness: 0.3, metalness: 0.4 }));
      esfera.position.y = alturaNodo;
      esfera.userData.indice = i;
      nodo.add(esfera);
      seleccionables.push(esfera);
      grupoMapa.add(nodo);
      mapaDept.push({ feature, grupo, malla, material, nodo, anillo, esfera, pulso: 0, hover: 0 });
      const datosNode = { ...feature.properties };
      datosNode.poblacionTexto = obtenerPoblacionTexto ? obtenerPoblacionTexto(datosNode) : (datosNode.poblacion || '-');
      datosNode.regalos = calcularRegalos ? calcularRegalos(datosNode) : Math.max(1, Math.round(Number(datosNode.poblacion || 0)/10000));
      nodosDept.push({ datos: datosNode, posicion: new THREE.Vector3(cap.x, alturaNodo + alturaMapa, cap.z), grupo: nodo, anillo, esfera, pulso: 0, hover: 0 });
      crearEtiquetaSector(feature, i);
    });
  }

  function construirRuta() {
    if (construirRutaFn) { construirRutaFn({ nodosDept, corregirDentro, curvaRutaRef: (v)=>{curvaRuta=v;}, grupoMapa, registrar, iniciarTramo, actualizarTrineoRef: actualizarTrineo, rutaOrdenRef: (v)=>{rutaOrden=v;nodosDept=v;}, THREE, CatmullRomCurve3: THREE.CatmullRomCurve3 }); return; }
    if (nodosDept.length === 0) return;
    const asuncion = nodosDept.find((n) => n.datos.nombre === 'Asunción' || n.datos.nombre === 'Madrid');
    const resto = nodosDept.filter((n) => n !== asuncion);
    const orden = asuncion ? [asuncion, ...resto] : nodosDept.slice();
    const puntos = orden.map((n) => { const p = n.posicion.clone(); p.y = alturaNodo + alturaMapa + 0.4; return corregirDentro(p); });
    curvaRuta = new THREE.CatmullRomCurve3(puntos, true, 'centripetal', 0.5);
    const muestras = curvaRuta.getPoints(420).map((p) => corregirDentro(p));
    curvaRuta = new THREE.CatmullRomCurve3(muestras, true, 'centripetal', 0.5);
    const geoSpline = new THREE.BufferGeometry().setFromPoints(curvaRuta.getPoints(300));
    grupoMapa.add(new THREE.Line(geoSpline, new THREE.LineBasicMaterial({ color: 0x00ffcc, transparent: true, opacity: 0.38 })));
    rutaOrden = orden; nodosDept = orden;
  }

  let indiceActual = 0, segmento = null;
  function iniciarTramo(t) {
    const hacia = (indiceActual + 1) % nodosDept.length;
    segmento = { inicio: t, destinoIdx: hacia };
    const destino = nodosDept[hacia].datos;
    mostrarSector(destino, (obtenerRegaloTexto ? obtenerRegaloTexto(destino, 'vuelo') : 'EN VUELO · ' + destino.regalos + ' REGALOS'));
    registrar('Ruta trazada hacia ' + (destino.romano ? destino.romano + ' ' : '') + destino.nombre + (destino.capital ? ' (' + destino.capital + ')' : ''), '');
  }

  function actualizarTrineo(t, dt) {
    if (!curvaRuta) return;
    if (!segmento) iniciarTramo(t);
    const k = Math.min(1, (t - segmento.inicio) / duracionTramo);
    const e = k * k * (3 - 2 * k);
    const u = ((indiceActual + e) / nodosDept.length) % 1;
    const pos = corregirDentro(curvaRuta.getPoint(u));
    trineo.position.copy(pos);
    trineo.position.y += Math.sin(t * 4.5) * 0.18;
    const adelanto = corregirDentro(curvaRuta.getPoint((u + 0.015) % 1));
    trineo.lookAt(adelanto.x, trineo.position.y, adelanto.z);
    if (auraTrineo) auraTrineo.position.copy(trineo.position);
    if (luzOro) luzOro.position.copy(trineo.position);
    if (pintarEstela) { if (Math.random() > 0.35) pintarEstela(trineo.position); actualizarEstela(dt); }
    nodosDept.forEach((nd) => {
      if (nd.pulso > 0) nd.pulso = Math.max(0, nd.pulso - dt * 1.6);
      if (nd.hover > 0) nd.hover = Math.max(0, nd.hover - dt * 3.0);
      const brillo = Math.max(nd.pulso, nd.hover * 0.6);
      if (nd.anillo) nd.anillo.scale.setScalar(1 + brillo * 0.9);
      if (nd.esfera) { nd.esfera.scale.setScalar(1 + brillo * 1.1); nd.esfera.material.emissiveIntensity = 1.4 + brillo * 2.2; }
    });
    const gi = invertir(pos.x, pos.z);
    document.getElementById('lat').textContent = gi.lat.toFixed(3);
    document.getElementById('lon').textContent = gi.lon.toFixed(3);
    if (t - ultimoControlSector > 0.12) {
      ultimoControlSector = t;
      let activo = -1;
      for (let i = 0; i < geojson.features.length; i++) {
        if (geoContains(geojson.features[i], [gi.lon, gi.lat])) { activo = i; break; }
      }
      if (activo >= 0 && activo !== indiceSectorActivo) {
        if (indiceSectorActivo >= 0) {
          const anterior = mapaDept[indiceSectorActivo];
          anterior.grupo.position.y = 0;
          anterior.material.emissiveIntensity = 0.16;
          anterior.material.opacity = 0.46;
        }
        indiceSectorActivo = activo;
        const actual = mapaDept[activo];
        actual.grupo.position.y = 0.9;
        actual.material.emissiveIntensity = 0.75;
        actual.material.opacity = 0.6;
        const d = actual.feature.properties;
        const datosNode = nodosDept.find((n) => n.datos === d || n.datos.nombre === d.nombre);
        mostrarSector(datosNode ? datosNode.datos : d, entregados.has(d.nombre) ? (obtenerRegaloTexto?obtenerRegaloTexto(d,'completo'):(d.regalos + ' / ' + d.regalos + ' ENTREGADOS')) : (obtenerRegaloTexto?obtenerRegaloTexto(d,'pendiente'):('PENDIENTE · ' + d.regalos + ' REGALOS')));
      }
    }
    if (k >= 1) {
      const destino = nodosDept[segmento.destinoIdx];
      if (destino) {
        destino.pulso = 1;
        if (!entregados.has(destino.datos.nombre)) {
          entregados.add(destino.datos.nombre);
          entregas++;
          regalosTotales += Number(destino.datos.regalos || 1);
        }
        actualizarEntrega();
        mostrarSector(destino.datos, obtenerRegaloTexto ? obtenerRegaloTexto(destino.datos,'completado') : 'SECTOR COMPLETADO · ' + destino.datos.regalos + ' / ' + destino.datos.regalos + ' REGALOS');
        registrar('Entrega confirmada en ' + (destino.datos.romano?destino.datos.romano+' ':'') + destino.datos.nombre + (destino.datos.capital?' · '+destino.datos.capital:'') + ' · ' + destino.datos.regalos + ' regalos · ' + destino.datos.capitalLat.toFixed(3) + ', ' + destino.datos.capitalLon.toFixed(3), 'oro');
      }
      indiceActual = segmento.destinoIdx;
      segmento = null;
    }
  }

  let capaEstela, COLOR_ESTELA = [0xff1e42, 0xffd700], poolsEstela = [[],[]], contadorEstela = 0;
  function pintarEstela(pos) {
    const idx = contadorEstela % 2; contadorEstela++;
    const pool = poolsEstela[idx];
    let p = pool.find((x) => !x.visible);
    if (!p) { p = new THREE.Mesh(new THREE.SphereGeometry(0.4, 6, 6), new THREE.MeshBasicMaterial({ color: COLOR_ESTELA[idx], transparent: true, opacity: 0.9, blending: THREE.AdditiveBlending })); capaEstela.add(p); pool.push(p); }
    p.visible = true; p.material.opacity = 0.9; p.userData.vida = 1; p.position.copy(pos); p.scale.setScalar(1);
  }
  function actualizarEstela(dt) {
    for (const pool of poolsEstela) {
      for (const p of pool) {
        if (!p.visible) continue;
        p.userData.vida -= dt * 0.5;
        if (p.userData.vida <= 0) { p.visible = false; continue; }
        p.material.opacity = 0.9 * p.userData.vida;
        p.scale.setScalar(0.4 + p.userData.vida);
      }
    }
  }

  function construirTrineoBasico() {
    const matCuerpo = new THREE.MeshStandardMaterial({ color: 0xff1e42, emissive: 0x5c000f, emissiveIntensity: 0.35, roughness: 0.18, metalness: 0.9 });
    const matOro = new THREE.MeshStandardMaterial({ color: 0xffd700, emissive: 0x6b4e00, emissiveIntensity: 0.45, roughness: 0.16, metalness: 1.0 });
    trineo = new THREE.Group();
    const s = new THREE.Shape();
    s.moveTo(-3.1,0); s.lineTo(2.7,0); s.quadraticCurveTo(3.3,0,3.5,0.45); s.quadraticCurveTo(3.8,1.0,3.7,1.5); s.quadraticCurveTo(3.55,2.1,3.0,2.18); s.quadraticCurveTo(2.45,2.25,2.35,1.8); s.quadraticCurveTo(2.3,1.52,2.6,1.45); s.lineTo(2.5,0.5); s.lineTo(-2.7,0.5); s.lineTo(-2.95,2.7); s.quadraticCurveTo(-3.45,3.0,-3.7,2.6); s.quadraticCurveTo(-3.95,2.15,-3.45,1.85); s.lineTo(-3.1,1.85); s.closePath();
    const prof = 3.8;
    const casco = new THREE.Mesh(new THREE.ExtrudeGeometry(s, { depth: prof, bevelEnabled: true, bevelThickness: 0.08, bevelSize: 0.08, bevelSegments: 3, curveSegments: 10 }), matCuerpo);
    casco.position.z = -prof/2;
    trineo.add(casco);
    [-1,1].forEach(lado=>{ const curva=new THREE.CubicBezierCurve3(new THREE.Vector3(-3.3,-0.5,0),new THREE.Vector3(0.7,-0.7,0),new THREE.Vector3(3.2,-0.7,0),new THREE.Vector3(3.95,1.3,0)); const runner=new THREE.Mesh(new THREE.TubeGeometry(curva,34,0.17,8,false),matOro); const g=new THREE.Group(); g.add(runner); g.position.z=lado*(prof/2-0.2); trineo.add(g); });
    const CAJA_ESTILOS=[{cuerpo:0x1fa84a,cinta:0xffffff},{cuerpo:0x1e5cff,cinta:0xffd700},{cuerpo:0xffd700,cinta:0xff1e42},{cuerpo:0xf4f8ff,cinta:0xff1e42}];
    let semilla=11; function rnd(){semilla=(semilla*16807)%2147483647; return semilla/2147483647;}
    for(let i=0;i<5;i++){const lado=0.75+rnd()*0.35;const altura=lado*(0.85+rnd()*0.25);const estilo=CAJA_ESTILOS[i%CAJA_ESTILOS.length];const caja=new THREE.Group();caja.add(new THREE.Mesh(new THREE.BoxGeometry(lado,altura,lado),new THREE.MeshStandardMaterial({color:estilo.cuerpo,roughness:0.45,metalness:0.15,emissive:estilo.cuerpo,emissiveIntensity:0.15})));const matCinta=new THREE.MeshStandardMaterial({color:estilo.cinta,roughness:0.25,metalness:0.5,emissive:estilo.cinta,emissiveIntensity:0.3});const aw=lado*0.16;caja.add(new THREE.Mesh(new THREE.BoxGeometry(aw,altura+0.05,lado+0.06),matCinta));caja.add(new THREE.Mesh(new THREE.BoxGeometry(lado+0.06,altura+0.05,aw),matCinta));caja.position.set((rnd()-0.5)*3,altura/2+0.55,(rnd()-0.5)*2.2);trineo.add(caja);}
    trineo.scale.setScalar(1.5);
    grupoMapa.add(trineo);
    auraTrineo = new THREE.PointLight(0xffd700, 1.4, 90); escena.add(auraTrineo);
    capaEstela = new THREE.Group(); escena.add(capaEstela);
  }

  function conectar() {
    let ultimoEvento = 0, enlaceRegistrado = false;
    const marcarReconexion = ()=>{ if(Date.now()-ultimoEvento<=3000)return; badge.classList.add('offline'); badgeTexto.textContent='RECONECTANDO'; };
    setInterval(marcarReconexion, 1000);
    const fuente = new EventSource('/api/vuelo/stream');
    fuente.onopen = ()=>{ ultimoEvento=Date.now(); badge.classList.remove('offline'); badgeTexto.textContent='EN LINEA'; if(!enlaceRegistrado){ enlaceRegistrado=true; registrar('Enlace de telemetria con el Polo Norte establecido',''); } };
    fuente.onmessage = (evento)=>{ ultimoEvento=Date.now(); badge.classList.remove('offline'); badgeTexto.textContent='EN LINEA'; const d=JSON.parse(evento.data); document.getElementById('alt').textContent=d.altitud.toFixed(0)+' m'; document.getElementById('vel').textContent=d.velocidadKmh.toFixed(0)+' km/h'; document.getElementById('sec').textContent=d.secuencia; };
    fuente.onerror = marcarReconexion;
  }

  function restablecerVista() {
    if (config.restablecerVista) { config.restablecerVista(camara, controles); return; }
    camara.position.set(0,150,110);
    controles.target.set(0,0,0);
    controles.update();
  }

  function prepararInteraccion() {
    const raycaster = new THREE.Raycaster();
    const puntero = new THREE.Vector2(9,9);
    let arrastre=false, inicioPuntero={x:0,y:0};
    const nodoBajoElPuntero = ()=>{
      raycaster.setFromCamera(puntero, camara);
      const impactos = raycaster.intersectObjects(seleccionables, false);
      return impactos.length>0?impactos[0].object.userData.indice:-1;
    };
    const canvas = renderizador.domElement;
    canvas.addEventListener('pointermove', (ev)=>{
      const rect=canvas.getBoundingClientRect();
      puntero.x=((ev.clientX-rect.left)/rect.width)*2-1;
      puntero.y=-((ev.clientY-rect.top)/rect.height)*2+1;
      const idx=nodoBajoElPuntero();
      canvas.style.cursor=idx>=0?'pointer':'grab';
      if(idx>=0){ const ndHover=nodosDept.find((n)=>n.esfera.userData.indice===idx)||nodosDept[idx]; if(ndHover) ndHover.hover=1; }
      if(Math.hypot(ev.clientX-inicioPuntero.x,ev.clientY-inicioPuntero.y)>6) arrastre=true;
    });
    canvas.addEventListener('pointerdown',(ev)=>{ arrastre=false; inicioPuntero={x:ev.clientX,y:ev.clientY}; });
    canvas.addEventListener('click',(ev)=>{
      if(arrastre)return;
      const rect=canvas.getBoundingClientRect();
      puntero.x=((ev.clientX-rect.left)/rect.width)*2-1; puntero.y=-((ev.clientY-rect.top)/rect.height)*2+1;
      const idx=nodoBajoElPuntero();
      if(idx<0)return;
      const nd=nodosDept.find((n)=>n.esfera.userData.indice===idx);
      if(!nd)return;
      nd.pulso=1;
      mostrarSector(nd.datos, (obtenerRegaloTexto?obtenerRegaloTexto(nd.datos,'consulta'):nd.datos.regalos+' / '+nd.datos.regalos+' REGALOS · CONSULTA TACTICA'));
      controles.target.set(nd.grupo.position.x,0,nd.grupo.position.z);
      registrar('Consulta tactica: '+(nd.datos.romano?nd.datos.romano+' ':'')+nd.datos.nombre+(nd.datos.capital?' · '+nd.datos.capital:'')+' · '+(nd.datos.poblacionTexto||'')+(nd.datos.regalos?' · '+nd.datos.regalos+' regalos programados':''),'oro');
    });
    const btn=document.getElementById('resetVista');
    if(btn) btn.addEventListener('click', restablecerVista);
  }

  async function iniciar() {
    try {
      const datos = await cargarJson(config.rutaDatos);
      geojson = datos; pais = datos;
      proyeccion = crearProyeccion ? crearProyeccion(geojson, anchoMapa, altoMapa) : geoMercator().fitSize([anchoMapa, altoMapa], geojson);
      if (centroPaisFn) centroPais = centroPaisFn(proyeccion, proyectar); else {
        const centro = geoCentroid(geojson);
        const cw = proyectar(centro[0], centro[1]);
        centroPais = new THREE.Vector3(cw.x, alturaNodo + alturaMapa, cw.z);
      }
      if (config.validarCapitales) config.validarCapitales(geojson, proyectar, invertir);
      else {
        for (const f of geojson.features) {
          const { capitalLat, capitalLon } = f.properties;
          if (capitalLat===undefined||capitalLon===undefined) continue;
          const q = proyectar(Number(capitalLon), Number(capitalLat));
          const r = invertir(q.x, q.z);
          const error = Math.hypot(r.lon - Number(capitalLon), r.lat - Number(capitalLat));
          if (!(error < 0.001)) throw new Error('Proyeccion inconsistente en ' + f.properties.nombre + ': ' + error);
        }
      }
      geojson.features.forEach((f) => {
        const p = f.properties;
        if (!p.poblacionTexto) p.poblacionTexto = obtenerPoblacionTexto ? obtenerPoblacionTexto(p) : (p.poblacion ? Number(p.poblacion).toLocaleString('es-ES') : '-');
        if (p.regalos === undefined) p.regalos = calcularRegalos ? calcularRegalos(p) : Math.max(1, Math.round(Number(p.poblacion || 0)/10000));
      });
      const canvas = document.getElementById('escena');
      escena = new THREE.Scene();
      escena.background = new THREE.Color(config.colorFondo || 0x02080a);
      escena.fog = new THREE.FogExp2(config.colorFondo || 0x02080a, config.neblina || 0.0028);
      camara = new THREE.PerspectiveCamera(45, innerWidth/innerHeight, 0.1, 3000);
      camara.position.copy(config.posicionCamara || new THREE.Vector3(0,150,110));
      renderizador = new THREE.WebGLRenderer({ canvas, antialias: true, alpha: true });
      renderizador.setPixelRatio(Math.min(devicePixelRatio,2));
      renderizador.setSize(innerWidth, innerHeight);
      renderizadorEtiquetas = new CSS2DRenderer();
      renderizadorEtiquetas.setSize(innerWidth, innerHeight);
      renderizadorEtiquetas.domElement.style.position='fixed'; renderizadorEtiquetas.domElement.style.top='0'; renderizadorEtiquetas.domElement.style.left='0'; renderizadorEtiquetas.domElement.style.pointerEvents='none';
      document.getElementById('etiquetas3d').appendChild(renderizadorEtiquetas.domElement);
      controles = new OrbitControls(camara, renderizador.domElement);
      controles.enableDamping = true; controles.dampingFactor = 0.06;
      controles.autoRotate = true; controles.autoRotateSpeed = 0.25;
      controles.minDistance = config.minDist || 90; controles.maxDistance = config.maxDist || 260;
      controles.maxPolarAngle = Math.PI/2.02;
      if (config.objetivoControles) controles.target.copy(config.objetivoControles); else controles.target.set(0,0,0);
      controles.update();
      escena.add(new THREE.AmbientLight(0x88ddff, 0.7));
      const luzDir = new THREE.DirectionalLight(0xffffff, 1.4); luzDir.position.set(80,140,90); escena.add(luzDir);
      const luzCian = new THREE.PointLight(0x00ffcc, 1.2, 600); luzCian.position.set(0,70,0); escena.add(luzCian);
      luzOro = new THREE.PointLight(0xffd700, 0.9, 400); luzOro.position.set(-70,40,-40); escena.add(luzOro);
      grupoMapa = new THREE.Group(); escena.add(grupoMapa);
      rejilla = new THREE.GridHelper(200, 40, 0x00ffcc, 0x0a3b3b);
      rejilla.material.transparent = true; rejilla.material.opacity = 0.16; rejilla.position.y = -0.2;
      grupoMapa.add(rejilla);
      construirMapa();
      if (config.construirExtras) config.construirExtras({ geojson, proyectar, invertir, escena, grupoMapa, alturaMapa, CSS2DObject });
      construirRuta();
      if (crearTrineoFn) crearTrineoFn({ grupoMapa, escena }); else { construirTrineoBasico(); }
      prepararInteraccion();
      capaCarga.classList.add('oculto');
      actualizarEntrega();
      if (geojson.features[0]) mostrarSector(geojson.features[0].properties, obtenerRegaloTexto?obtenerRegaloTexto(geojson.features[0].properties,'pendiente'):('PENDIENTE · ' + geojson.features[0].properties.regalos + ' REGALOS'));
      registrar('Radar tactico inicializado · ' + geojson.features.length + ' entidades desde GeoJSON real', 'oro');
      conectar();
      addEventListener('resize', ()=>{ camara.aspect=innerWidth/innerHeight; camara.updateProjectionMatrix(); renderizador.setSize(innerWidth,innerHeight); renderizadorEtiquetas.setSize(innerWidth,innerHeight); });
      const reloj = new THREE.Clock();
      function animar(){ const dt=Math.min(reloj.getDelta(),0.05); const t=reloj.getElapsedTime(); controles.update(); actualizarTrineo(t,dt); rejilla.material.opacity=0.18+Math.sin(t*1.4)*0.05; luzCian.intensity=1.0+Math.sin(t*1.3)*0.3; renderizador.render(escena,camara); renderizadorEtiquetas.render(escena,camara); }
      renderizador.setAnimationLoop(animar);
    } catch (error) {
      if (capaCarga) { capaCarga.textContent = 'ERROR DE CARTOGRAFÍA: ' + error.message; }
      if (badge) badge.classList.add('offline');
      if (badgeTexto) badgeTexto.textContent = 'SIN DATOS';
      registrar('Fallo al cargar la cartografia real: ' + error.message, 'rojo');
    }
  }
  iniciar();
  return { iniciar, restablecerVista };
}
