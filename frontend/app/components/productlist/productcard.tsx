import {   Card,
  CardAction,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle, } from "../ui/card";

function ProductCard({name, product_type, rating, img }: {name: string, product_type?: string, img?: string, rating: number}) {
    return <Card className="w-full shadow-xl hover:shadow-2xl transition">
      <CardHeader>
        <img src={img} alt={name} width={150} height={150} className="mx-auto mb-3 rounded-2xl shadow-lg "/>
        <CardTitle>{name}</CardTitle>
        <CardDescription>{rating}</CardDescription>
        </CardHeader>
        <CardFooter className="mt-auto y-0">
            <CardAction className="w-full text-center bg-blue-500 text-white rounded-lg py-2 hover:bg-blue-600 transition">
            Mehr ansehen
            </CardAction>
        </CardFooter>
        </Card>
    }
export {
  ProductCard
}