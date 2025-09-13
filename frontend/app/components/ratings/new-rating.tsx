import { useState } from "react";
import { PiPencilLine } from "react-icons/pi";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "~/components/ui/dialog";

export default function NewRating({ productId }: { productId: number }) {
  const [guest, setGuest] = useState(false);
  const [username, setUsername] = useState("");
  const [rating, setRating] = useState(0);
  const [summary, setSummary] = useState("");
  const [ratingText, setReview] = useState("");

  async function handleSubmit(e) {
    e.preventDefault();

    const payload = { username, rating, summary, ratingText, guest };

    try {
      const res = await fetch(
        `http://localhost:8080/produkt/${productId}/reviews/add`,
        {
          method: "POST",
          mode: "cors",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(payload),
        }
      );

      if (!res.ok) {
        const text = await res.text();
        throw new Error(text || `HTTP ${res.status}`);
      }

      // Antwort kann JSON sein; falls leer, fallback
      const contentType = res.headers.get("Content-Type") || "";
      const data = contentType.includes("application/json")
        ? await res.json()
        : null;

      // TODO: handle success (z. B. UI-Feedback)
      console.log("Gespeichert:", data);
    } catch (err) {
      // TODO: handle error (z. B. Fehlermeldung anzeigen)
      console.error("Fehler beim Speichern:", err);
    }
  }

  return (
    <Dialog>
      <DialogTrigger asChild>
        <button className="flex items-center gap-2 bg-green-200 text-grey px-4 py-1 rounded-lg hover:text-white hover:bg-green-600 transition">
          <PiPencilLine></PiPencilLine>
          Bewertung schreiben
        </button>
      </DialogTrigger>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>Bewertung schreiben</DialogTitle>
          <DialogDescription>
            Hier kannst du eine Bewertung für das Produkt schreiben.
          </DialogDescription>
        </DialogHeader>
        <form className="grid gap-4 py-4">
          <div className="grid gap-2">
            <div className="gap-2 flex items-center">
              <input
                type="checkbox"
                name="guest"
                id="guest"
                checked={guest}
                onChange={(e) => setGuest(e.target.checked)}
              />
              <label className="font-semibold" htmlFor="guest">
                Als Gast bewerten?
              </label>
            </div>

            <label
              className="font-semibold disabled:opacity-20"
              htmlFor="username"
            >
              Nutzername
            </label>
            <input
              type="text"
              id="username"
              name="username"
              className="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-green-500 disabled:opacity-50"
              disabled={guest}
              onChange={(e) => setUsername(e.target.value)}
            ></input>
          </div>
          <div className="grid gap-2">
            <label className="font-semibold" htmlFor="rating">
              Bewertung (1-5 Sterne)
            </label>

            <input
              type="number"
              id="rating"
              name="rating"
              min={0}
              max={5}
              className="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-green-500"
              required
              onChange={(e) => setRating(Number(e.target.value))}
            />
          </div>
          <div className="grid gap-2">
            <label className="font-semibold" htmlFor="summary">
              Zusammenfassung
            </label>
            <input
              type="text"
              id="summary"
              name="summary"
              className="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-green-500"
              required
              onChange={(e) => setSummary(e.target.value)}
            />
          </div>
          <div className="grid gap-2">
            <label className="font-semibold" htmlFor="rating-text">
              Rezension
            </label>
            <textarea
              id="rating-text"
              name="rating-text"
              rows={10}
              className="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-green-500"
              required
              onChange={(e) => setReview(e.target.value)}
            ></textarea>
          </div>
          <button
            type="submit"
            onClick={handleSubmit}
            className="w-full border-1 border-green-500 text-black rounded-lg py-2 hover:bg-green-600 hover:text-white transition"
          >
            Bewertung absenden
          </button>
        </form>
      </DialogContent>
    </Dialog>
  );
}
