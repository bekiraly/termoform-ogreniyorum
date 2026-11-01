// Offline support: the page is fetched from the network first so updates arrive at once;
// the last copy (and the fonts) are kept in a cache for use without internet.
const C = "tfo-v1";
self.addEventListener("install", e => { e.waitUntil(caches.open(C).then(c => c.addAll(["./", "./manifest.webmanifest", "../icon-192.png"])).then(() => self.skipWaiting())); });
self.addEventListener("activate", e => { e.waitUntil(caches.keys().then(ks => Promise.all(ks.filter(k => k !== C).map(k => caches.delete(k)))).then(() => self.clients.claim())); });
self.addEventListener("fetch", e => {
  const r = e.request; if (r.method !== "GET") return;
  const u = new URL(r.url);
  if (r.mode === "navigate") {
    e.respondWith(fetch(r).then(res => { const cp = res.clone(); caches.open(C).then(c => c.put("./", cp)); return res; })
      .catch(() => caches.match("./", {ignoreSearch: true})));
    return;
  }
  if (u.origin === location.origin || /fonts\.(googleapis|gstatic)\.com$/.test(u.hostname)) {
    e.respondWith(caches.match(r).then(hit => {
      const net = fetch(r).then(res => { if (res.ok || res.type === "opaque") { const cp = res.clone(); caches.open(C).then(c => c.put(r, cp)); } return res; }).catch(() => hit);
      return hit || net;
    }));
  }
});
