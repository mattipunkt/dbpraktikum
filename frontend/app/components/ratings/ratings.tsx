import { PiPencilLine, PiUserBold } from "react-icons/pi";
import NewRating from "./new-rating";

type Kunde = {
  id: number;
  gast: boolean;
  vorname: string;
  nachname: string;
  username: string;
};

type Rating = {
  rezension: string;
  zusammenfassung: string;
  sterne: number;
  hilfreich: number;
  datum: Date;
  kunde: Kunde;
};

export default function Ratings({
  ratings,
  productId,
}: {
  ratings: Rating[];
  productId: number;
}) {
  return (
    <div>
      <div className="flex justify-between items-center mb-4">
        <div className="text-xl font-bold my-0">Bewertungen</div>
        <div>
          <NewRating productId={productId} />
        </div>
      </div>
      {ratings.map((rating, index) => (
        <div key={index} className="border-b last:border-0 py-4">
          <div className="flex justify-between items-center mb-2">
            <div className="flex items-center gap-2 text-lg">
              <PiUserBold></PiUserBold>
              {rating.kunde.username ?? "Gast"}
            </div>
            <div className="text-yellow-500">
              {"★".repeat(rating.sterne)}
              {"☆".repeat(5 - rating.sterne)}
            </div>
          </div>
          <div className="mb-2">
            <h3 className="font-semibold text-md">{rating.zusammenfassung}</h3>
            <p className="text-gray-700">{rating.rezension}</p>
          </div>
          <div className="text-sm text-gray-500">
            {new Date(rating.datum).toLocaleDateString()}
            {" • "}
            {rating.hilfreich ?? 0} hilfreich
          </div>
        </div>
      ))}
    </div>
  );
}
