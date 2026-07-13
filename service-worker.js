const CACHE_NAME = "fitpals-v1";

const files = [
"/FitPalsWebsite/",
"/FitPalsWebsite/index.html",
"/FitPalsWebsite/about.html",
"/FitPalsWebsite/contact.html",
"/FitPalsWebsite/booking.html",
"/FitPalsWebsite/merchandise.html",
"/FitPalsWebsite/admin.html"
];

self.addEventListener("install", event => {
event.waitUntil(
caches.open(CACHE_NAME)
.then(cache => cache.addAll(files))
);
});


self.addEventListener("fetch", event => {
event.respondWith(
caches.match(event.request)
.then(response => response || fetch(event.request))
);
});
