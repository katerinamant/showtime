package com.example.showtime.FaqPage;

import com.example.showtime.Utils.Faq;

import java.util.ArrayList;
import java.util.List;

public class FaqPresenter {
    public List<Faq> getFaqCategory(int faqCategoryId) {
        Faq faq;
        ArrayList<Faq> res = new ArrayList<>();

        switch (faqCategoryId) {
            case 1:
                // Booking & Confirmation

                faq = new Faq();
                faq.setQuestion("How do I book a ticket?");
                faq.setAnswer("Simply tell UsherBot which show and date you’d like (e.g., “Book 2 tickets for Saturday’s show”). It will confirm availability and ask for your name and phone number.");
                res.add(faq);

                faq = new Faq();
                faq.setQuestion("Can I select specific seats?");
                faq.setAnswer("You can choose a seating section (red, yellow, blue, or green -accessible seating), but individual seat numbers aren’t currently available. UsherBot reserves your spot in the chosen section.\n\n" +
                        "Red seats are right upfront and yellow seats are right behind them. Blue seats are on the sides, in rows of three and are slightly elevated.");
                res.add(faq);

                faq = new Faq();
                faq.setQuestion("Do I have to keep my reservation code?");
                faq.setAnswer("Yes! Once your booking is complete, you’ll see a unique code in the chat. Keep it handy for any changes, cancellations, or refunds.");
                res.add(faq);

                faq = new Faq();
                faq.setQuestion("I need accessible seating. Can UsherBot help me?");
                faq.setAnswer("Absolutely. Just let UsherBot know you need accessible seating, and it will reserve your seats in the designated seating area -one seat for you and one for a companion");
                res.add(faq);

                faq = new Faq();
                faq.setQuestion("Why do I have to give my phone number?");
                faq.setAnswer("We use your phone number to confirm your identity, avoid duplicate bookings, and help you recover or change a reservation if you lose your code.");
                res.add(faq);
                break;

            case 2:
                // Pricing & Payment

                faq = new Faq();
                faq.setQuestion("What are the ticket prices?");
                faq.setAnswer("Red seats are right upfront and cost 40€ each.\n\n" +
                        "Yellow seats are right behind them -these cost 30€.\n\n" +
                        "Blue seats are on the sides and are slightly elevated -these cost 25€ each.\n\n" +
                        "Accessible seating is in a designated part of the red section and these cost 25€ each.\n\n"+
                        "Feel free to ask UsherBot for more details and it will confirm the exact price when booking.");
                res.add(faq);

                faq = new Faq();
                faq.setQuestion("Can I pay online or only at the booth?");
                faq.setAnswer("Currently, you’ll pay at the theater’s ticket booth. Show your reservation code when you arrive and our staff will handle the payment. We plan to add online payment soon!");
                res.add(faq);
                break;

            case 3:
                // Managing my booking

                faq = new Faq();
                faq.setQuestion("How do I change my booking?");
                faq.setAnswer("Give UsherBot your reservation code and let it know the new show date or time you’d prefer. If seats are available, it updates your booking right away.");
                res.add(faq);

                faq = new Faq();
                faq.setQuestion("What if I typed the wrong date?");
                faq.setAnswer("If it’s not the same day of the show, simply provide UsherBot your reservation code and correct date/time.\n\n" +
                        "If it’s already show day, you’ll need to contact support.");
                res.add(faq);

                faq = new Faq();
                faq.setQuestion("What if I need to cancel last-minute?");
                faq.setAnswer("Same-day cancellations aren’t allowed through UsherBot. If you have an urgent situation, please call our support line or visit the booth in person.");
                res.add(faq);

                faq = new Faq();
                faq.setQuestion("What if I forget my reservation code?");
                faq.setAnswer("Provide UsherBot with the phone number you used when booking. If it still can’t find your code or you don't have the phone number, you may need to call our support line for further assistance.");
                res.add(faq);

                faq = new Faq();
                faq.setQuestion("How do I rate my show?");
                faq.setAnswer("After your show, ask UsherBot to show you a 0–5 star scale. Just pick the star you want and confirm.");
                res.add(faq);

                faq = new Faq();
                faq.setQuestion("Can I edit my rating after submitting?");
                faq.setAnswer("Once you confirm your star rating, it’s final—so choose carefully.");
                res.add(faq);
                break;

            case 4:
                // Show Information

                faq = new Faq();
                faq.setQuestion("Which days are shows playing?");
                faq.setAnswer("We typically run shows Tuesday through Sunday (afternoon and evening). Mondays are our rest day.");
                res.add(faq);

                faq = new Faq();
                faq.setQuestion("What times are the shows?");
                faq.setAnswer("We do multiple shows everyday!\n\n" +
                        "\"Peter Pan\" has two shows -afternoon show at 5:00PM and evening show at 8:00PM.\n\n" +
                        "\"Romeo and Juliet\" also has an afternoon show at 6:00PM and an evening show at 9:00PM.");
                res.add(faq);

                faq = new Faq();
                faq.setQuestion("What are the shows like?");
                faq.setAnswer("\"Peter Pan\" is a lively, adventure-filled performance by an ensemble cast and it runs for about 2 hours. It is ideal for families with children aged 5 and up, thanks to its playful storytelling, colorful costumes, and energetic music.\n\n" +
                        "\"Romeo and Juliet\" is a retelling of the classic Shakespearean drama performed by a talented ensemble. Its duration is about 2.5 hours. It's considered more suitable for teens and adults, given the romantic and dramatic themes. It offers emotional performances, beautifully designed sets, and a timeless love story.");
                res.add(faq);

                faq = new Faq();
                faq.setQuestion("Who are the actors or performers?");
                faq.setAnswer("When you ask about a specific show, UsherBot can give you a short list of the main cast or performers, if available. Just say \"Who is starring in Romeo and Juliet?\" or ask \"Tell me about the actors for Friday’s show.\"");
                res.add(faq);
                break;

            case 5:
                // Troubleshooting & Support

                faq = new Faq();
                faq.setQuestion("UsherBot won’t understand my input. What do I do?");
                faq.setAnswer("Try rephrasing your request or use some of the suggested commands. If you’re still stuck, you can request a human agent.");
                res.add(faq);

                faq = new Faq();
                faq.setQuestion("How do I speak to a real person?");
                faq.setAnswer("Ask UsherBot to connect you to a support agent (during our operating hours -Monday to Friday 8:00AM-8:00PM).\n\n" +
                        "You can also call our support line at 210-0000000.");
                res.add(faq);

                faq = new Faq();
                faq.setQuestion("What if I have a more complex request?");
                faq.setAnswer("If UsherBot can’t handle your specific needs (e.g., large group bookings, special accommodations), it will offer to transfer you to a human agent or provide our support line. We’re here to help!");
                res.add(faq);
                break;

            default:
                break;
        }

        return res;
    }
}
