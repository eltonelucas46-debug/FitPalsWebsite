(function () {
  "use strict";

  let deferredPrompt = null;

  const installButton = document.createElement("button");

  installButton.id = "fitpals-install-button";
  installButton.type = "button";
  installButton.textContent = "📱 Install FitPals";

  Object.assign(installButton.style, {
    position: "fixed",
    bottom: "20px",
    right: "20px",
    zIndex: "99999",
    display: "block",
    padding: "12px 18px",
    border: "none",
    borderRadius: "999px",
    background: "#2196f3",
    color: "#fff",
    fontSize: "15px",
    fontWeight: "700",
    boxShadow: "0 6px 20px rgba(0,0,0,.25)",
    cursor: "pointer"
  });

  document.addEventListener("DOMContentLoaded", function () {
    document.body.appendChild(installButton);
  });

  // Browser's native PWA installation event
  window.addEventListener("beforeinstallprompt", function (event) {
    event.preventDefault();

    deferredPrompt = event;

    installButton.textContent = "📱 Install FitPals";
    installButton.style.display = "block";
  });

  installButton.addEventListener("click", async function () {

    // Use the browser's native PWA installation prompt when available.
    if (deferredPrompt) {
      deferredPrompt.prompt();

      try {
        await deferredPrompt.userChoice;
      } catch (error) {
        console.log("FitPals installation prompt closed.");
      }

      deferredPrompt = null;
      installButton.style.display = "none";

      return;
    }

    // Otherwise provide the Android APK.
    const downloadAPK = confirm(
      "Install FitPals on Android?\n\n" +
      "Your browser will download the FitPals Android app (APK). " +
      "Android will ask you to confirm the installation."
    );

    if (downloadAPK) {
      window.location.href = "/FitPalsWebsite/FitPals-Animated.apk";
    }
  });

  // Hide the button after successful PWA installation.
  window.addEventListener("appinstalled", function () {
    installButton.style.display = "none";
    deferredPrompt = null;

    console.log("FitPals was installed.");
  });

})();
